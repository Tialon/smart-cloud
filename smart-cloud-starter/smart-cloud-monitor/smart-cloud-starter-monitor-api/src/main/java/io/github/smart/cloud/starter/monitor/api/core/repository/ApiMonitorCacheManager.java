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

import io.github.smart.cloud.starter.monitor.api.dto.ApiRequestSummaryDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.utility.concurrent.NamedThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * 接口监控信息存储
 *
 * @author collin
 * @date 2025-09-20
 */
@RequiredArgsConstructor
public class ApiMonitorCacheManager implements InitializingBean, DisposableBean, ApplicationListener<RefreshScopeRefreshedEvent> {

    private final ApiMonitorProperties apiMonitorProperties;

    /**
     * 接口访问记录
     */
    @Getter
    private final ConcurrentMap<String, ApiRequestSummaryDTO> apiRequestSummaryMap = new ConcurrentHashMap<>();
    private final InitApiRequestSummaryFunction initApiRequestSummaryFunction = new InitApiRequestSummaryFunction();
    private ScheduledExecutorService cleanSchedule;

    /**
     * 保存接口访问记录
     *
     * @param event
     */
    public void process(ApiMonitorEvent event) {
        ApiRequestSummaryDTO apiRequestSummary = getApiRequestSummaryDTO(event.getApiName());
        apiRequestSummary.getTotalCount().increment();
    }

    /**
     * 获取单个接口访问记录
     *
     * @param apiName
     * @return
     */
    public ApiRequestSummaryDTO getApiRequestSummaryDTO(String apiName) {
        return apiRequestSummaryMap.computeIfAbsent(apiName, initApiRequestSummaryFunction);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cleanSchedule = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("clean-api-record-cache"));
        cleanSchedule.scheduleWithFixedDelay(this::clearApiRecords, apiMonitorProperties.getCleanIntervalSeconds(),
                apiMonitorProperties.getCleanIntervalSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (cleanSchedule != null) {
            cleanSchedule.shutdown();
        }

        clearApiRecords();
    }

    public void clearApiRecords() {
        apiRequestSummaryMap.clear();
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // 处理“@RefreshScope会导致ScheduledExecutorService失效”的问题
        // do nothing
    }

    /**
     * 初始化ApiRequestSummaryDTO
     *
     * @author collin
     * @date 2024-01-7
     */
    private class InitApiRequestSummaryFunction implements Function<String, ApiRequestSummaryDTO> {

        @Override
        public ApiRequestSummaryDTO apply(String s) {
            ApiRequestSummaryDTO apiRequestSummary = new ApiRequestSummaryDTO();
            apiRequestSummary.setTotalCount(new LongAdder());
            apiRequestSummary.setFailCount(new LongAdder());
            apiRequestSummary.setSlowCount(new LongAdder());
            apiRequestSummary.setMaxCost(0L);
            return apiRequestSummary;
        }

    }

}