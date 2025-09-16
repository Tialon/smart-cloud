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
package io.github.smart.cloud.starter.monitor.api.autoconfigure;

import io.github.smart.cloud.starter.monitor.api.annotation.ConditionApiMonitor;
import io.github.smart.cloud.starter.monitor.api.annotation.ConditionWeworkRobotNotice;
import io.github.smart.cloud.starter.monitor.api.component.IApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.component.SlowApiChecker;
import io.github.smart.cloud.starter.monitor.api.component.SlowApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.component.WeworkRobotComponent;
import io.github.smart.cloud.starter.monitor.api.listener.alert.SlowApiWeworkAlertListener;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.SlowApiMonitorListener;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 慢接口监控配置
 *
 * @author collin
 * @date 2024-01-16
 */
@Configuration
@ConditionApiMonitor
@ConditionalOnProperty(prefix = ApiMonitorProperties.PREFIX, name = "slow-api-monitor.enable", havingValue = "true", matchIfMissing = true)
public class SlowApiMonitorAutoConfiguration {

    @Bean
    @RefreshScope
    public SlowApiMonitorRepository slowApiMonitorRepository(final ApiMonitorProperties apiMonitorProperties) {
        return new SlowApiMonitorRepository(apiMonitorProperties);
    }

    @Bean
    public SlowApiMonitorListener slowApiMonitorListener(final IApiMonitorRepository slowApiMonitorRepository) {
        return new SlowApiMonitorListener(slowApiMonitorRepository);
    }

    @Bean
    @RefreshScope
    public SlowApiChecker slowApiChecker(final ApiMonitorProperties apiMonitorProperties,
                                         final SlowApiMonitorRepository slowApiMonitorRepository,
                                         final ApplicationEventPublisher applicationEventPublisher) {
        return new SlowApiChecker(apiMonitorProperties, slowApiMonitorRepository, applicationEventPublisher);
    }

    @Bean
    @ConditionWeworkRobotNotice
    public SlowApiWeworkAlertListener slowApiWeworkAlertListener(final WeworkRobotComponent weworkRobotComponent,
                                                                 final ApiMonitorProperties apiMonitorProperties) {
        return new SlowApiWeworkAlertListener(weworkRobotComponent, apiMonitorProperties);
    }

}