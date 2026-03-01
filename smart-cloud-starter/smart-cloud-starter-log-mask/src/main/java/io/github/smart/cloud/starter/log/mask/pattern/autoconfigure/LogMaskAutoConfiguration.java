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

@Configuration
public class LogMaskAutoConfiguration {

    // 设置高优先级，尽早初始化配置信息
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