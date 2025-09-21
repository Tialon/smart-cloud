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

import io.github.smart.cloud.starter.monitor.api.core.IApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.dto.ApiRequestSummaryDTO;
import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.SlowApiMonitorProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * 慢接口监控数据存储
 *
 * @author collin.li
 * @datge 2025-09-19
 */
@Slf4j
@RequiredArgsConstructor
public class SlowApiMonitorRepository implements IApiMonitorRepository<ApiSlowAlertDTO> {

    private final ApiMonitorProperties apiMonitorProperties;
    private final ApiMonitorCacheManager apiMonitorCacheManager;

    @Override
    public void process(ApiMonitorEvent event) {
        try {
            SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
            if (slowApiMonitorProperties.getApiWhiteList().contains(event.getApiName())) {
                return;
            }

            ApiRequestSummaryDTO apiRequestSummary = apiMonitorCacheManager.getApiRequestSummaryDTO(event.getApiName());
            if (event.getCost() >= slowApiMonitorProperties.getCostThreshold(event.getApiName())) {
                apiRequestSummary.getSlowCount().increment();
                if (event.getCost() > apiRequestSummary.getMaxCost()) {
                    synchronized (apiRequestSummary) {
                        apiRequestSummary.setMaxCost(Math.max(apiRequestSummary.getMaxCost(), event.getCost()));
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("api slow info add error|name={}", event.getApiName(), ex);
        }
    }

    @Override
    public List<ApiSlowAlertDTO> getAlertRecords() {
        ConcurrentMap<String, ApiRequestSummaryDTO> apiRequestSummaryMap = apiMonitorCacheManager.getApiRequestSummaryMap();
        if (apiRequestSummaryMap.isEmpty()) {
            return Collections.emptyList();
        }

        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        List<ApiSlowAlertDTO> apiSlowAlerts = new ArrayList<>(0);
        for (Map.Entry<String, ApiRequestSummaryDTO> entry : apiRequestSummaryMap.entrySet()) {
            String apiName = entry.getKey();
            ApiRequestSummaryDTO apiRequestSummary = entry.getValue();
            if (apiRequestSummary.getMaxCost() >= slowApiMonitorProperties.getAlertCostThreshold()) {
                ApiSlowAlertDTO apiSlowAlert = new ApiSlowAlertDTO();
                apiSlowAlert.setName(apiName);
                apiSlowAlert.setSlowCount(apiRequestSummary.getSlowCount().sum());
                apiSlowAlert.setTotalCount(apiRequestSummary.getTotalCount().sum());
                apiSlowAlert.setMaxCost(apiRequestSummary.getMaxCost());

                apiSlowAlerts.add(apiSlowAlert);
                continue;
            }

            long slowCountSum = apiRequestSummary.getSlowCount().sum();
            if (slowCountSum == 0) {
                continue;
            }

            BigDecimal slowRateThreshold = slowApiMonitorProperties.getSlowRateThreshold(apiName);
            long totalCount = apiRequestSummary.getTotalCount().sum();
            BigDecimal slowRate = BigDecimal.valueOf(slowCountSum).divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP);
            if (slowRate.compareTo(slowRateThreshold) >= 0) {
                ApiSlowAlertDTO apiSlowAlert = new ApiSlowAlertDTO();
                apiSlowAlert.setName(apiName);
                apiSlowAlert.setSlowCount(slowCountSum);
                apiSlowAlert.setTotalCount(totalCount);
                apiSlowAlert.setMaxCost(apiRequestSummary.getMaxCost());
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

}