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

import io.github.smart.cloud.monitor.common.WeworkRobotAgent;
import io.github.smart.cloud.starter.monitor.api.annotation.ConditionApiMonitor;
import io.github.smart.cloud.starter.monitor.api.annotation.ConditionWeworkRobotNotice;
import io.github.smart.cloud.starter.monitor.api.core.IApiMonitorDataProcessor;
import io.github.smart.cloud.starter.monitor.api.core.check.SlowApiChecker;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.core.data.SlowApiMonitorDataProcessor;
import io.github.smart.cloud.starter.monitor.api.core.message.SlowApiMessageFactory;
import io.github.smart.cloud.starter.monitor.api.listener.alert.SlowAbstractApiMonitorWeworkAlertListener;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.SlowApiMonitorListener;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    @ConditionalOnMissingBean
    public SlowApiMonitorDataProcessor slowApiMonitorRepository(final ApiMonitorProperties apiMonitorProperties,
                                                                final ApiMonitorCacheManager apiMonitorCacheManager,
                                                                final ApplicationEventPublisher applicationEventPublisher) {
        return new SlowApiMonitorDataProcessor(apiMonitorProperties, apiMonitorCacheManager, applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public SlowApiMonitorListener slowApiMonitorListener(final IApiMonitorDataProcessor slowApiMonitorRepository) {
        return new SlowApiMonitorListener(slowApiMonitorRepository);
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean
    public SlowApiChecker slowApiChecker(final ApiMonitorProperties apiMonitorProperties,
                                         final ApiMonitorCacheManager apiMonitorCacheManager,
                                         final SlowApiMonitorDataProcessor slowApiMonitorRepository,
                                         final ApplicationEventPublisher applicationEventPublisher) {
        return new SlowApiChecker(apiMonitorProperties, apiMonitorCacheManager, slowApiMonitorRepository, applicationEventPublisher);
    }

    @Bean
    @ConditionWeworkRobotNotice
    public SlowApiMessageFactory slowApiMessageFactory(final ApiMonitorProperties apiMonitorProperties) {
        return new SlowApiMessageFactory(apiMonitorProperties);
    }

    @Bean
    @ConditionWeworkRobotNotice
    @ConditionalOnMissingBean
    public SlowAbstractApiMonitorWeworkAlertListener slowApiMonitorWeworkAlertListener(final WeworkRobotAgent weworkRobotAgent,
                                                                                       final ApiMonitorProperties apiMonitorProperties,
                                                                                       final SlowApiMessageFactory slowApiMessageFactory) {
        return new SlowAbstractApiMonitorWeworkAlertListener(weworkRobotAgent, apiMonitorProperties, slowApiMessageFactory);
    }

}