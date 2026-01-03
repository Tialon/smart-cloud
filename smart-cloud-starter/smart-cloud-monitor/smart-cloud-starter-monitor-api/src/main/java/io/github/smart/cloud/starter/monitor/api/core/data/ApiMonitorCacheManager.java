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
package io.github.smart.cloud.starter.monitor.api.core.data;

import io.github.smart.cloud.starter.monitor.api.dto.ApiRequestSummaryDTO;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.utility.concurrent.NamedThreadFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.*;

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
    private final ConcurrentMap<String, ApiRequestSummaryDTO> apiRequestSummaryCache = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanSchedule;

    /**
     * 获取单个接口访问记录
     *
     * @param apiName
     * @return
     */
    public ApiRequestSummaryDTO getApiRequestSummaryDTO(String apiName) {
        return apiRequestSummaryCache.computeIfAbsent(apiName, key -> {
            ApiRequestSummaryDTO apiRequestSummary = new ApiRequestSummaryDTO();
            apiRequestSummary.setErrorAlerted(false);
            apiRequestSummary.setSlowAlerted(false);
            return apiRequestSummary;
        });
    }

    /**
     * 是否超过最大缓存大小
     *
     * @return
     */
    public boolean isExceedCacheSize() {
        return apiRequestSummaryCache.size() >= apiMonitorProperties.getApiRequestSummaryCacheMaxSize();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cleanSchedule = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("clean-api-record-cache"));
        cleanSchedule.scheduleWithFixedDelay(this::clearCache, apiMonitorProperties.getCleanIntervalSeconds(),
                apiMonitorProperties.getCleanIntervalSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (cleanSchedule != null) {
            cleanSchedule.shutdownNow();
        }

        clearCache();
    }

    public void clearCache() {
        apiRequestSummaryCache.clear();
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // 处理“@RefreshScope会导致ScheduledExecutorService失效”的问题
        // do nothing
    }

}