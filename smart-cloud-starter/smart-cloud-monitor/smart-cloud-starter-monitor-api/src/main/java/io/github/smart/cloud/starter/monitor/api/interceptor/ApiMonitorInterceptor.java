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
import io.github.smart.cloud.starter.monitor.api.enums.MonitorType;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.utility.concurrent.NamedThreadFactory;
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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private ThreadPoolExecutor apiMonitorEventConsumerThreadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(),
            new NamedThreadFactory("api-monitor-event-consumer"), new ThreadPoolExecutor.AbortPolicy());

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
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

                Method method = invocation.getMethod();
                ApiMonitor apiMonitor = AnnotationUtils.findAnnotation(method, ApiMonitor.class);
                String apiName = getApiName(method, apiMonitor);

                ApiMonitorEvent apiMonitorEvent = new ApiMonitorEvent(this);
                apiMonitorEvent.setApiName(apiName);
                apiMonitorEvent.setCost(System.currentTimeMillis() - startTime);
                apiMonitorEvent.setThrowable(throwable);
                apiMonitorEvent.setTraceId(traceId);
                apiMonitorEvent.setMonitorType(apiMonitor == null ? MonitorType.ALL : apiMonitor.monitorType());
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
        apiMonitorEventConsumerThreadPool.execute(() -> {
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
        });
    }

    /**
     * 获取类标志符
     *
     * @param method
     * @param apiMonitor
     * @return
     */
    private String getApiName(Method method, ApiMonitor apiMonitor) {
        String methodName = method.getName();
        if (apiMonitor != null && StringUtils.isNotBlank(apiMonitor.apiName())) {
            methodName = apiMonitor.apiName();
        }
        return method.getDeclaringClass().getSimpleName() + "#" + methodName;
    }

}