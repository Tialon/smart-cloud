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
package io.github.smart.cloud.starter.monitor.api.interceptor;

import brave.Tracing;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import io.github.smart.cloud.starter.monitor.api.annotation.ApiMonitor;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 接口健康监控
 *
 * @author collin
 * @date 2024-01-15
 */
@Slf4j
@RequiredArgsConstructor
public class ApiMonitorInterceptor implements MethodInterceptor, InitializingBean {

    private final ApiMonitorProperties apiMonitorProperties;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApiMonitorCacheManager apiMonitorCacheManager;
    private final Tracing tracing;
    private LinkedBlockingQueue<ApiMonitorEvent> apiMonitorEventQueue = null;
    private Thread apiMonitorEventQueueConsumerThread = null;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        String apiName = getApiName(invocation.getMethod());
        Object result = null;
        Throwable throwable = null;
        try {
            result = invocation.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            try {
                String traceId = Optional.ofNullable(tracing)
                        .map(Tracing::currentTraceContext)
                        .map(CurrentTraceContext::get)
                        .map(TraceContext::traceIdString)
                        .orElse(null);

                ApiMonitorEvent apiMonitorEvent = new ApiMonitorEvent(this);
                apiMonitorEvent.setApiName(apiName);
                apiMonitorEvent.setCost(System.currentTimeMillis() - startTime);
                apiMonitorEvent.setThrowable(throwable);
                apiMonitorEvent.setTraceId(traceId);
                if (!apiMonitorEventQueue.offer(apiMonitorEvent)) {
                    log.warn("ApiMonitorEvent queue is full, discard event: {}", apiMonitorEvent);
                }
            } catch (Exception e) {
                log.error("offer ApiMonitorEvent to queue fail", e);
            }
        }

        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        apiMonitorEventQueue = new LinkedBlockingQueue<>(apiMonitorProperties.getApiMonitorEventQueueSize());

        // 消费队列
        apiMonitorEventQueueConsumerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 从队列中取日志（阻塞等待，直到有数据）
                    ApiMonitorEvent apiMonitorEvent = apiMonitorEventQueue.take();
                    if (apiMonitorCacheManager.isExceedCacheSize()) {
                        log.warn("ApiRequestSummary cache size exceed max size[{}], discard event: {}", apiMonitorProperties.getApiRequestSummaryCacheMaxSize(), apiMonitorEvent);
                        continue;
                    }
                    applicationEventPublisher.publishEvent(apiMonitorEvent);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("publishEvent apiMonitorEvent fail", e);
                }
            }
        }, "api-monitor-event-consumer-thread");

        apiMonitorEventQueueConsumerThread.start();
    }

    /**
     * 获取类标志符
     *
     * @param method
     * @return
     */
    private String getApiName(Method method) {
        String methodName = method.getName();
        ApiMonitor apiMonitor = AnnotationUtils.findAnnotation(method, ApiMonitor.class);
        if (apiMonitor != null && StringUtils.isNotBlank(apiMonitor.apiName())) {
            methodName = apiMonitor.apiName();
        }
        return method.getDeclaringClass().getSimpleName() + "#" + methodName;
    }

}