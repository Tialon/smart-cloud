package io.github.smart.cloud.starter.log.mask.pattern.pointcut;

import io.github.smart.cloud.starter.log.mask.pattern.annotation.LogMask;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;

/**
 * 日志脱敏切入点
 *
 * @author collin.li
 * @date 2025-12-23
 */
public class LogMaskPointcut extends StaticMethodMatcherPointcut {

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        // 接口或实现类上有注解
        return AnnotatedElementUtils.isAnnotated(method, LogMask.class)
                || AnnotatedElementUtils.hasAnnotation(targetClass, LogMask.class);
    }

}