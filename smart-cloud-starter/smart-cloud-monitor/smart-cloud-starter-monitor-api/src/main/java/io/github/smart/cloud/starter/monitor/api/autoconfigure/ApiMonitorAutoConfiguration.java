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

import brave.Tracing;
import io.github.smart.cloud.starter.monitor.api.annotation.ConditionApiMonitor;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiMonitorCacheManager;
import io.github.smart.cloud.starter.monitor.api.core.data.ApiTotalCountMonitorDataProcessor;
import io.github.smart.cloud.starter.monitor.api.interceptor.ApiMonitorInterceptor;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.ApiTotalCountMonitorListener;
import io.github.smart.cloud.starter.monitor.api.pointcut.ApiMonitorPointCut;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * monitor-api自动配置类
 *
 * @author collin
 * @date 2024-01-16
 */
@Configuration
@ConditionApiMonitor
public class ApiMonitorAutoConfiguration {

    @Bean
    @RefreshScope
    @ConfigurationProperties(prefix = ApiMonitorProperties.PREFIX)
    public ApiMonitorProperties apiMonitorProperties() {
        return new ApiMonitorProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiMonitorPointCut apiMonitorPointCut(ApiMonitorProperties apiMonitorProperties) {
        return new ApiMonitorPointCut(apiMonitorProperties.isPointCutSupportMappingAnnotation());
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiMonitorInterceptor apiMonitorInterceptor(final ApiMonitorProperties apiMonitorProperties,
                                                       final ApplicationEventPublisher applicationEventPublisher,
                                                       final ApiMonitorCacheManager apiMonitorCacheManager,
                                                       @Autowired(required = false) Tracing tracing) {
        return new ApiMonitorInterceptor(apiMonitorProperties, applicationEventPublisher, apiMonitorCacheManager, tracing);
    }

    @Bean
    public Advisor apiMonitorAdvisor(final ApiMonitorInterceptor apiMonitorInterceptor, final ApiMonitorPointCut apiMonitorPointCut) {
        DefaultBeanFactoryPointcutAdvisor apiMonitorAdvisor = new DefaultBeanFactoryPointcutAdvisor();
        apiMonitorAdvisor.setAdvice(apiMonitorInterceptor);
        apiMonitorAdvisor.setPointcut(apiMonitorPointCut);

        return apiMonitorAdvisor;
    }

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean
    public ApiMonitorCacheManager apiMonitorCacheManager(final ApiMonitorProperties apiMonitorProperties) {
        return new ApiMonitorCacheManager(apiMonitorProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiTotalCountMonitorDataProcessor apiTotalCountMonitorDataProcessor(final ApiMonitorCacheManager apiMonitorCacheManager) {
        return new ApiTotalCountMonitorDataProcessor(apiMonitorCacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiTotalCountMonitorListener apiMonitorListener(final ApiTotalCountMonitorDataProcessor apiTotalCountMonitorDataProcessor) {
        return new ApiTotalCountMonitorListener(apiTotalCountMonitorDataProcessor);
    }

}