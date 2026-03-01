package io.github.smart.cloud.starter.log.mask.pattern.util;

import io.github.smart.cloud.starter.log.mask.LogMaskConstants;
import io.github.smart.cloud.starter.log.mask.pattern.PatternConfig;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 脱敏工具类
 *
 * @author collin.li
 * @date 2025-12-22
 */
public class PatternUtil {

    /**
     * 脱敏（使用默认正则表达式、默认脱敏字段匹配）
     *
     * @param content
     * @return
     */
    public static String mask(String content) {
        if (content == null) {
            return null;
        }

        return mask(content, PatternConfig.REGEX_PATTERN, PatternConfig.getFieldNameSet());
    }

    /**
     * 指定正则表达式、匹配字段脱敏
     *
     * @param content
     * @param pattern
     * @param names
     * @return
     */
    public static String mask(String content, Pattern pattern, Set<String> names) {
        if (content == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            // key + 分割符 + value
            String key = matcher.group(1);
            if (names == null || names.contains(key.toLowerCase())) {
                String separator = matcher.group(2);
                matcher.appendReplacement(sb, key + separator + LogMaskConstants.MASK_VALUE);
            }
        }
        return matcher.appendTail(sb).toString();
    }

}