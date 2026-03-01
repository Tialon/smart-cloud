package io.github.smart.cloud.starter.log.mask.pattern.util;

import io.github.smart.cloud.starter.log.mask.pattern.PatternConfig;

import java.util.regex.Pattern;

/**
 * 日志脱敏上下文
 *
 * @author collin.li
 * @date 2025-12-23
 */
public class LogMaskContext {

    public static final ThreadLocal<Pattern> CONTEXT = new ThreadLocal<>();


    /**
     * 为当前上下文设置匹配的正则表达式
     *
     * @param regex
     */
    public static void set(String regex) {
        CONTEXT.set(PatternConfig.convert(regex));
    }

    /**
     * 获取当前上下文的正则匹配
     *
     * @return
     */
    public static Pattern get() {
        return CONTEXT.get();
    }

    /**
     * 清理上下文
     */
    public static void remove() {
        CONTEXT.remove();
    }

}