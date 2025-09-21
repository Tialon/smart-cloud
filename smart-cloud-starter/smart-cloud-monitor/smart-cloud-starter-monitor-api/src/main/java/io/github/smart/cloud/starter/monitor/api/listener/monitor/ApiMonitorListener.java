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
package io.github.smart.cloud.starter.monitor.api.listener.monitor;

import io.github.smart.cloud.starter.monitor.api.constants.OrderConstants;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;

/**
 * 接口监控监听处理器
 *
 * @author collin
 * @date 2025-09-20
 */
@RequiredArgsConstructor
public class ApiMonitorListener implements ApplicationListener<ApiMonitorEvent>, Ordered {

    private final ApiMonitorCacheManager apiMonitorCacheManager;

    @Override
    public void onApplicationEvent(ApiMonitorEvent event) {
        apiMonitorCacheManager.process(event);
    }

    @Override
    public int getOrder() {
        return OrderConstants.API_MONITOR_LISTENER;
    }

}