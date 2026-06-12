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
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import lombok.RequiredArgsConstructor;

/**
 * 接口监控请求总数信息处理
 *
 * @author collin
 * @date 2025-09-20
 */
@RequiredArgsConstructor
public class ApiTotalCountMonitorDataProcessor {

    private final ApiMonitorCacheManager apiMonitorCacheManager;

    /**
     * 保存接口访问记录
     *
     * @param event
     */
    public void process(ApiMonitorEvent event) {
        ApiRequestSummaryDTO apiRequestSummary = apiMonitorCacheManager.getApiRequestSummaryDTO(event.getApiName());
        apiRequestSummary.setTotalCount(apiRequestSummary.getTotalCount() + 1);
    }

}