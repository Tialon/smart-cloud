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
import io.github.smart.cloud.starter.monitor.api.core.IApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.core.WeworkRobotComponent;
import io.github.smart.cloud.starter.monitor.api.core.check.ExceptionApiChecker;
import io.github.smart.cloud.starter.monitor.api.core.repository.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.core.repository.ExceptionApiMonitorRepository;
import io.github.smart.cloud.starter.monitor.api.listener.alert.ApiExceptionWeworkAlertListener;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.ExceptionApiMonitorListener;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 异常接口监控配置
 *
 * @author collin
 * @date 2024-01-16
 */
@Configuration
@ConditionApiMonitor
@ConditionalOnProperty(prefix = ApiMonitorProperties.PREFIX, name = "exception-api-monitor.enable", havingValue = "true", matchIfMissing = true)
public class ExceptionApiMonitorAutoConfiguration {

    @Bean
    public ExceptionApiMonitorRepository exceptionApiMonitorRepository(final ApiMonitorProperties apiMonitorProperties,
                                                                       final ApiMonitorCacheManager apiMonitorCacheManager) {
        return new ExceptionApiMonitorRepository(apiMonitorProperties, apiMonitorCacheManager);
    }

    @Bean
    public ExceptionApiMonitorListener exceptionApiMonitorListener(final IApiMonitorRepository exceptionApiMonitorRepository) {
        return new ExceptionApiMonitorListener(exceptionApiMonitorRepository);
    }

    @Bean
    @RefreshScope
    public ExceptionApiChecker exceptionApiChecker(final ApiMonitorProperties apiMonitorProperties,
                                                   final ExceptionApiMonitorRepository exceptionApiMonitorRepository,
                                                   final ApplicationEventPublisher applicationEventPublisher) {
        return new ExceptionApiChecker(apiMonitorProperties, exceptionApiMonitorRepository, applicationEventPublisher);
    }

    @Bean
    @ConditionWeworkRobotNotice
    public ApiExceptionWeworkAlertListener apiExceptionWeworkAlertListener(final WeworkRobotComponent weworkRobotComponent,
                                                                           final ApiMonitorProperties apiMonitorProperties) {
        return new ApiExceptionWeworkAlertListener(weworkRobotComponent, apiMonitorProperties);
    }

}