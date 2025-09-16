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
package io.github.smart.cloud.starter.monitor.api.component;

import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.dto.SlowApiMonitorCacheDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.SlowApiMonitorProperties;
import io.github.smart.cloud.utility.concurrent.NamedThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * 慢接口监控数据存储
 *
 * @author collin.li
 * @datge 2025-09-19
 */
@Slf4j
@RequiredArgsConstructor
public class SlowApiMonitorRepository implements IApiMonitorRepository<ApiSlowAlertDTO>, InitializingBean, DisposableBean, ApplicationListener<RefreshScopeRefreshedEvent> {

    private final ApiMonitorProperties apiMonitorProperties;
    /**
     * 接口成功失败记录统计
     */
    private final ConcurrentMap<String, SlowApiMonitorCacheDTO> apiHistory = new ConcurrentHashMap<>();
    private final CreateSlowApiMonitorCacheDtoFunction createSlowApiMonitorCacheDtoFunction = new CreateSlowApiMonitorCacheDtoFunction();
    private ScheduledExecutorService cleanSchedule;

    @Override
    public void saveRequestLog(ApiMonitorEvent event) {
        try {
            SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
            if (slowApiMonitorProperties.getApiWhiteList().contains(event.getApiName())) {
                return;
            }

            SlowApiMonitorCacheDTO slowApiMonitorCacheDTO = apiHistory.computeIfAbsent(event.getApiName(), createSlowApiMonitorCacheDtoFunction);
            slowApiMonitorCacheDTO.getTotalCount().increment();
            if (event.getCost() >= slowApiMonitorProperties.getCostThreshold(event.getApiName())) {
                slowApiMonitorCacheDTO.getSlowCount().increment();
                if (event.getCost() > slowApiMonitorCacheDTO.getMaxCost()) {
                    synchronized (slowApiMonitorCacheDTO) {
                        slowApiMonitorCacheDTO.setMaxCost(Math.max(slowApiMonitorCacheDTO.getMaxCost(), event.getCost()));
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("api slow info add error|name={}", event.getApiName(), ex);
        }
    }

    @Override
    public List<ApiSlowAlertDTO> getAlertRecords() {
        if (apiHistory.isEmpty()) {
            return Collections.emptyList();
        }

        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        List<ApiSlowAlertDTO> apiSlowAlerts = new ArrayList<>(0);
        for (Map.Entry<String, SlowApiMonitorCacheDTO> entry : apiHistory.entrySet()) {
            String apiName = entry.getKey();
            SlowApiMonitorCacheDTO slowApiMonitorCacheDTO = entry.getValue();
            if (slowApiMonitorCacheDTO.getMaxCost() >= slowApiMonitorProperties.getAlertCostThreshold()) {
                ApiSlowAlertDTO apiSlowAlert = new ApiSlowAlertDTO();
                apiSlowAlert.setName(apiName);
                apiSlowAlert.setSlowCount(slowApiMonitorCacheDTO.getSlowCount().sum());
                apiSlowAlert.setTotalCount(slowApiMonitorCacheDTO.getTotalCount().sum());
                apiSlowAlert.setMaxCost(slowApiMonitorCacheDTO.getMaxCost());

                apiSlowAlerts.add(apiSlowAlert);
                continue;
            }

            long slowCountSum = slowApiMonitorCacheDTO.getSlowCount().sum();
            if (slowCountSum == 0) {
                continue;
            }

            BigDecimal slowRateThreshold = slowApiMonitorProperties.getSlowRateThreshold(apiName);
            BigDecimal slowRate = BigDecimal.valueOf(slowCountSum).divide(BigDecimal.valueOf(slowApiMonitorCacheDTO.getTotalCount().sum()), 4, RoundingMode.HALF_UP);
            if (slowRate.compareTo(slowRateThreshold) >= 0) {
                ApiSlowAlertDTO apiSlowAlert = new ApiSlowAlertDTO();
                apiSlowAlert.setName(apiName);
                apiSlowAlert.setSlowCount(slowApiMonitorCacheDTO.getSlowCount().sum());
                apiSlowAlert.setTotalCount(slowApiMonitorCacheDTO.getTotalCount().sum());
                apiSlowAlert.setMaxCost(slowApiMonitorCacheDTO.getMaxCost());
                apiSlowAlert.setSlowRate(slowRate);

                apiSlowAlerts.add(apiSlowAlert);
            }
        }

        if (!apiSlowAlerts.isEmpty()) {
            if (apiSlowAlerts.size() > slowApiMonitorProperties.getApiReportMaxCount()) {
                /**
                 * slowRate为null的排前面，不为null的排后面
                 * slowRate都为null时，按maxCost降序
                 * slowRate都不为null时，按slowRate降序
                 */
                Collections.sort(apiSlowAlerts, (a, b) -> {
                    if (a.getSlowRate() == null && b.getSlowRate() != null) {
                        return -1;
                    }
                    if (a.getSlowRate() != null && b.getSlowRate() == null) {
                        return 1;
                    }
                    if (a.getSlowRate() == null && b.getSlowRate() == null) {
                        return Long.compare(b.getMaxCost(), a.getMaxCost());
                    }
                    return b.getSlowRate().compareTo(a.getSlowRate());
                });
                // 取前N个
                return apiSlowAlerts.subList(0, slowApiMonitorProperties.getApiReportMaxCount());
            }
        }

        return apiSlowAlerts;
    }

    @Override
    public void destroy() throws Exception {
        if (cleanSchedule != null) {
            cleanSchedule.shutdown();
        }

        clearApiHistory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        cleanSchedule = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("clean-slow-api-cache"));
        cleanSchedule.scheduleWithFixedDelay(this::clearApiHistory, slowApiMonitorProperties.getCleanIntervalSeconds(), slowApiMonitorProperties.getCleanIntervalSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {

    }

    public void clearApiHistory() {
        apiHistory.clear();
    }

    /**
     * 创建SlowApiMonitorCacheDTO
     *
     * @author collin
     * @date 2025-09-16
     */
    private class CreateSlowApiMonitorCacheDtoFunction implements Function<String, SlowApiMonitorCacheDTO> {

        @Override
        public SlowApiMonitorCacheDTO apply(String s) {
            return new SlowApiMonitorCacheDTO(new LongAdder(), new LongAdder(), 0L);
        }

    }

}