/*
 * Copyright © 2019 collin (1634753825@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.smart.cloud.starter.monitor.api.core.repository;

import io.github.smart.cloud.exception.AbstractBaseException;
import io.github.smart.cloud.starter.monitor.api.core.IApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.dto.ApiExceptionAlertDTO;
import io.github.smart.cloud.starter.monitor.api.dto.ApiRequestSummaryDTO;
import io.github.smart.cloud.starter.monitor.api.enums.ApiExceptionRemindType;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.ExceptionApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.util.PercentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import sun.security.provider.certpath.SunCertPathBuilderException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.cert.CertPathValidatorException;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * 异常接口监控信息存储
 *
 * @author collin
 * @date 2024-01-6
 */
@Slf4j
@RequiredArgsConstructor
public class ExceptionApiMonitorRepository implements IApiMonitorRepository<ApiExceptionAlertDTO>, InitializingBean {

    private final ApiMonitorProperties apiMonitorProperties;
    private final ApiMonitorCacheManager apiMonitorCacheManager;
    private Set<String> needAlertExceptionClassNames;

    /**
     * 添加接口访问记录
     *
     * @param event
     */
    @Override
    public void process(ApiMonitorEvent event) {
        try {
            ExceptionApiMonitorProperties exceptionApiMonitorProperties = apiMonitorProperties.getExceptionApiMonitor();
            if (exceptionApiMonitorProperties.getApiWhiteList().contains(event.getApiName())) {
                return;
            }

            ApiRequestSummaryDTO apiRequestSummaryDTO = apiMonitorCacheManager.getApiRequestSummaryDTO(event.getApiName());
            if (event.getThrowable() != null) {
                apiRequestSummaryDTO.getFailCount().increment();
                Throwable throwable = apiRequestSummaryDTO.getThrowable();
                synchronized (apiRequestSummaryDTO) {
                    if (throwable == null) {
                        apiRequestSummaryDTO.setThrowable(event.getThrowable());
                    } else {
                        // 如果当前异常为需要提醒的类或code，则不更新
                        Set<String> needAlertExceptionClassNames = exceptionApiMonitorProperties.getNeedAlertExceptionClassNames();
                        if (needAlertExceptionClassNames.contains(throwable.getClass().getSimpleName())) {
                            return;
                        }
                        if (throwable instanceof AbstractBaseException) {
                            Set<String> needAlertExceptionCodes = exceptionApiMonitorProperties.getNeedAlertExceptionCodes();
                            if (needAlertExceptionCodes.contains(((AbstractBaseException) throwable).getCode())) {
                                return;
                            }
                        }

                        apiRequestSummaryDTO.setThrowable(event.getThrowable());
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("api health info add error|name={}", event.getApiName(), ex);
        }
    }

    /**
     * 查询不健康的接口信息
     *
     * @return
     */
    @Override
    public List<ApiExceptionAlertDTO> getAlertRecords() {
        ConcurrentMap<String, ApiRequestSummaryDTO> apiRequestSummaryMap = apiMonitorCacheManager.getApiRequestSummaryMap();
        if (apiRequestSummaryMap.isEmpty()) {
            return Collections.emptyList();
        }

        List<ApiExceptionAlertDTO> apiExceptions = new ArrayList<>(0);
        for (Map.Entry<String, ApiRequestSummaryDTO> entry : apiRequestSummaryMap.entrySet()) {
            String name = entry.getKey();
            ApiRequestSummaryDTO apiRequestSummary = entry.getValue();
            long failCountSum = apiRequestSummary.getFailCount().sum();
            if (failCountSum == 0) {
                continue;
            }

            BigDecimal failCount = BigDecimal.valueOf(failCountSum);
            BigDecimal total = BigDecimal.valueOf(apiRequestSummary.getTotalCount().sum());
            BigDecimal failRate = failCount.divide(total, 4, RoundingMode.HALF_UP);
            ApiExceptionRemindType remindType = match(name, total, failRate, apiRequestSummary.getThrowable());
            if (remindType != ApiExceptionRemindType.NONE) {
                ApiExceptionAlertDTO apiExceptionAlertDTO = new ApiExceptionAlertDTO();
                apiExceptionAlertDTO.setName(name);
                apiExceptionAlertDTO.setTotalCount(total.longValue());
                apiExceptionAlertDTO.setFailCount(failCount.longValue());
                apiExceptionAlertDTO.setFailRate(PercentUtil.format(failRate));
                apiExceptionAlertDTO.setThrowable(apiRequestSummary.getThrowable());
                apiExceptionAlertDTO.setRemindType(remindType);
                apiExceptions.add(apiExceptionAlertDTO);
            }
        }

        if (!apiExceptions.isEmpty()) {
            ExceptionApiMonitorProperties exceptionApiMonitorProperties = apiMonitorProperties.getExceptionApiMonitor();
            if (apiExceptions.size() > exceptionApiMonitorProperties.getApiReportMaxCount()) {
                Collections.sort(apiExceptions, (o1, o2) -> {
                    ApiExceptionRemindType remindType1 = o1.getRemindType();
                    ApiExceptionRemindType remindType2 = o2.getRemindType();
                    // 异常信息类型排在前
                    if (ApiExceptionRemindType.EXCEPTION_INFO == remindType1 && ApiExceptionRemindType.EXCEPTION_INFO != remindType2) {
                        return 1;
                    }
                    if (ApiExceptionRemindType.EXCEPTION_INFO != remindType1 && ApiExceptionRemindType.EXCEPTION_INFO == remindType2) {
                        return -1;
                    }

                    // 按失败率倒叙排序
                    return (int) (o2.getFailCount() * o1.getTotalCount() - o1.getFailCount() * o2.getTotalCount());
                });
                return apiExceptions.subList(0, exceptionApiMonitorProperties.getApiReportMaxCount());
            }
        }
        return apiExceptions;
    }

    /**
     * 判断是否不健康
     *
     * @param total
     * @param name
     * @param failRate
     * @param throwable
     * @return
     */
    private ApiExceptionRemindType match(String name, BigDecimal total, BigDecimal failRate, Throwable throwable) {
        ExceptionApiMonitorProperties exceptionApiMonitorProperties = apiMonitorProperties.getExceptionApiMonitor();
        if (exceptionApiMonitorProperties.getAlertExceptionMarked()) {
            if (!CollectionUtils.isEmpty(exceptionApiMonitorProperties.getNeedAlertExceptionCodes())) {
                if (throwable instanceof AbstractBaseException) {
                    AbstractBaseException exception = (AbstractBaseException) throwable;
                    if (exceptionApiMonitorProperties.getNeedAlertExceptionCodes().contains(exception.getCode())) {
                        return ApiExceptionRemindType.EXCEPTION_INFO;
                    }
                }
            }

            if (!CollectionUtils.isEmpty(needAlertExceptionClassNames)) {
                for (String needAlertExceptionClassName : needAlertExceptionClassNames) {
                    if (throwable.toString().contains(needAlertExceptionClassName)) {
                        return ApiExceptionRemindType.EXCEPTION_INFO;
                    }
                }
            }
        }

        BigDecimal failRateThreshold = exceptionApiMonitorProperties.getFailRateThresholds().getOrDefault(name, exceptionApiMonitorProperties.getDefaultFailRateThreshold());
        if (total.intValue() >= exceptionApiMonitorProperties.getMatchMinCount() && failRate.compareTo(failRateThreshold) >= 0) {
            return ApiExceptionRemindType.FAIL_RATE;

        }
        return ApiExceptionRemindType.NONE;
    }

    @Override
    public void afterPropertiesSet() {
        ExceptionApiMonitorProperties exceptionApiMonitorProperties = apiMonitorProperties.getExceptionApiMonitor();
        needAlertExceptionClassNames = exceptionApiMonitorProperties.getNeedAlertExceptionClassNames();
        if (CollectionUtils.isEmpty(needAlertExceptionClassNames)) {
            Set<String> defaultNeedAlertExceptionClassNames = new HashSet<>(32);
            defaultNeedAlertExceptionClassNames.add(SQLException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(SQLTimeoutException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(NumberFormatException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(ConcurrentModificationException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(NullPointerException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(IndexOutOfBoundsException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(ArrayIndexOutOfBoundsException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(StringIndexOutOfBoundsException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(ArrayStoreException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(ClassCastException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(ClassNotFoundException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(StackOverflowError.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(OutOfMemoryError.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(IllegalMonitorStateException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(CertPathValidatorException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(SunCertPathBuilderException.class.getSimpleName());
            defaultNeedAlertExceptionClassNames.add(NoSuchElementException.class.getSimpleName());

            needAlertExceptionClassNames = defaultNeedAlertExceptionClassNames;
        }
    }

}