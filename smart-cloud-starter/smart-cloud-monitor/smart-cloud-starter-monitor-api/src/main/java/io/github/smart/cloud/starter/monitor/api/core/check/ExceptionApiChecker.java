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
package io.github.smart.cloud.starter.monitor.api.core.check;

import io.github.smart.cloud.starter.monitor.api.core.IApiMonitorDataProcessor;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.dto.ApiExceptionAlertDTO;
import io.github.smart.cloud.starter.monitor.api.dto.ApiRequestSummaryDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorAlertEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.ExceptionApiMonitorProperties;
import io.github.smart.cloud.utility.concurrent.NamedThreadFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异常接口监控，企业微信机器人通知
 *
 * @author collin
 * @date 2024-04-28
 */
@RequiredArgsConstructor
public class ExceptionApiChecker implements InitializingBean, DisposableBean, ApplicationListener<RefreshScopeRefreshedEvent> {

    private final ApiMonitorProperties apiMonitorProperties;
    private final ApiMonitorCacheManager apiMonitorCacheManager;
    private final IApiMonitorDataProcessor exceptionApiMonitorDataProcessor;
    private final ApplicationEventPublisher applicationEventPublisher;
    private ScheduledExecutorService exceptionApiCheckSchedule;

    @Override
    public void afterPropertiesSet() throws Exception {
        exceptionApiCheckSchedule = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("exception-api-notice-schedule"));

        ExceptionApiMonitorProperties exceptionApiMonitorProperties = apiMonitorProperties.getExceptionApiMonitor();
        exceptionApiCheckSchedule.scheduleWithFixedDelay(this::checkExceptionApiAndNotice, exceptionApiMonitorProperties.getNoticeIntervalSeconds(),
                exceptionApiMonitorProperties.getNoticeIntervalSeconds(), TimeUnit.SECONDS);
    }

    /**
     * 检测异常接口，并发送通知
     */
    public void checkExceptionApiAndNotice() {
        List<ApiExceptionAlertDTO> apiExceptions = exceptionApiMonitorDataProcessor.getAlertRecords();
        if (apiExceptions.isEmpty()) {
            return;
        }

        boolean needAlert = apiExceptions.stream()
                .filter(e -> !e.isAlerted())
                .findFirst()
                .isPresent();
        if (!needAlert) {
            return;
        }

        // 标记已告警
        apiExceptions.forEach(e -> {
            ApiRequestSummaryDTO apiRequestSummary = apiMonitorCacheManager.getApiRequestSummaryDTO(e.getName());
            if (!apiRequestSummary.isErrorAlerted()) {
                apiRequestSummary.setErrorAlerted(true);
            }
        });

        applicationEventPublisher.publishEvent(ApiMonitorAlertEvent.buildSummaryEvents(this, apiExceptions));
    }

    @Override
    public void destroy() throws Exception {
        if (exceptionApiCheckSchedule != null) {
            exceptionApiCheckSchedule.shutdownNow();
        }
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // 处理“@RefreshScope会导致ScheduledExecutorService失效”的问题
        // do nothing
    }

}