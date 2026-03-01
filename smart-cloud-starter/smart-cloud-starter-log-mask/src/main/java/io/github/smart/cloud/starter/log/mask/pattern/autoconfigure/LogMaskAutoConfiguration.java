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
package io.github.smart.cloud.starter.log.mask.pattern.autoconfigure;

import io.github.smart.cloud.starter.log.mask.pattern.interceptor.LogMaskInterceptor;
import io.github.smart.cloud.starter.log.mask.pattern.pointcut.LogMaskPointcut;
import io.github.smart.cloud.starter.log.mask.pattern.properties.LogMaskProperties;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * 日志脱敏自动配置类
 *
 * @author collin.li
 * @date 2026-03-01
 */
@Configuration
public class LogMaskAutoConfiguration {

    @Bean
    @RefreshScope
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConfigurationProperties(prefix = "finsmart.log-mask")
    public LogMaskProperties logMaskProperties() {
        return new LogMaskProperties();
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public LogMaskInterceptor logMaskInterceptor(final LogMaskProperties logMaskProperties) {
        return new LogMaskInterceptor(logMaskProperties);
    }

    @Bean
    public LogMaskPointcut logMaskPointcut() {
        return new LogMaskPointcut();
    }

    @Bean
    public Advisor maskLogAdvisor(final LogMaskInterceptor logMaskInterceptor, LogMaskPointcut logMaskPointcut) {
        DefaultBeanFactoryPointcutAdvisor maskAdvisor = new DefaultBeanFactoryPointcutAdvisor();
        maskAdvisor.setAdvice(logMaskInterceptor);
        maskAdvisor.setPointcut(logMaskPointcut);

        return maskAdvisor;
    }

}