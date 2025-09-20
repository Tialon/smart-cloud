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
package io.github.smart.cloud.starter.monitor.api.component.check;

import io.github.smart.cloud.starter.monitor.api.component.repository.SlowApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.event.SlowApiAlertEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.SlowApiMonitorProperties;
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
 * 慢接口监控，企业微信机器人通知
 *
 * @author collin
 * @date 2025-09-18
 */
@RequiredArgsConstructor
public class SlowApiChecker implements InitializingBean, DisposableBean, ApplicationListener<RefreshScopeRefreshedEvent> {

    private final ApiMonitorProperties apiMonitorProperties;
    private final SlowApiMonitorRepository slowApiMonitorRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private ScheduledExecutorService slowApiCheckSchedule;

    @Override
    public void afterPropertiesSet() throws Exception {
        slowApiCheckSchedule = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory("slow-api-notice-schedule"));

        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        slowApiCheckSchedule.scheduleWithFixedDelay(this::checkSlowApiAndNotice, slowApiMonitorProperties.getSlowApiNoticeIntervalSeconds(),
                slowApiMonitorProperties.getSlowApiNoticeIntervalSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void destroy() throws Exception {
        if (slowApiCheckSchedule != null) {
            slowApiCheckSchedule.shutdown();
        }
    }

    /**
     * 检测异常接口，并发送通知
     */
    public void checkSlowApiAndNotice() {
        List<ApiSlowAlertDTO> apiSlowAlerts = slowApiMonitorRepository.getAlertRecords();
        if (apiSlowAlerts.isEmpty()) {
            return;
        }

        applicationEventPublisher.publishEvent(new SlowApiAlertEvent(this, apiSlowAlerts));
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // 处理“@RefreshScope会导致ScheduledExecutorService失效”的问题
        // do nothing
    }

}