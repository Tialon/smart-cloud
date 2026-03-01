package io.github.smart.cloud.starter.log.mask.pattern.util;

import io.github.smart.cloud.starter.log.mask.pattern.PatternConfig;
import io.github.smart.cloud.starter.log.mask.pattern.enums.MaskMode;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * 日志脱敏工具类
 *
 * @author collin.li
 * @date 2026-03-01
 */
@Slf4j
public class LogMaskUtil {

    public static String mask(String formattedMessage) {
        try {
            // 1、不脱敏
            if (null == PatternConfig.getMode() || MaskMode.OFF.getValue().compareTo(PatternConfig.getMode()) == 0) {
                return formattedMessage;
            }

            // 2、脱敏
            Pattern pattern = LogMaskContext.get();
            if (pattern == null) {
                if (MaskMode.ANNOTATION.getValue() == PatternConfig.getMode()) {
                    // 上下文中没标注，则不脱敏
                    return formattedMessage;
                }
                // 全脱敏
                else if (MaskMode.FULL.getValue() == PatternConfig.getMode()) {
                    return PatternUtil.mask(formattedMessage);
                }

                // 不脱敏
                return formattedMessage;
            }

            // 指定正则表达式的
            if (PatternConfig.REGEX_PATTERN == pattern) {
                // 使用默认内置字段脱敏
                return PatternUtil.mask(formattedMessage, pattern, PatternConfig.getFieldNameSet());
            } else {
                // 自定义正则表达式脱敏
                return PatternUtil.mask(formattedMessage, pattern, null);
            }
        } catch (Exception e) {
            log.warn("mask error|content={}", formattedMessage, e);
            return formattedMessage;
        }
    }

}