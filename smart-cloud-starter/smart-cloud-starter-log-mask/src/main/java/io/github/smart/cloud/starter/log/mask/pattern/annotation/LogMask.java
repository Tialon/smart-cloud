package io.github.smart.cloud.starter.log.mask.pattern.annotation;

import java.lang.annotation.*;

/**
 * 日志脱敏注解
 *
 * @author collin.li
 * @date 2025-12-23
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LogMask {

    /**
     * 要脱敏的正则表达式（当regex有值时），不配置则使用默认正则表达式
     *
     * @return
     */
    String regex() default "";

    /**
     * 方法执行完后是否立即清理上下文
     * <li>true：方法拦截器中执行完后是否清理上下文，非http请求，如：定时任务、mq消费者场景</li>
     * <li>false：将在HttpFilter中清理上下文</li>
     *
     * @return
     */
    boolean cleanAfter() default true;

}