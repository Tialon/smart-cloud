package io.github.smart.cloud.starter.log.mask.pattern.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脱敏模式
 *
 * @author collin.li
 * @date 2025-12-24
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MaskMode {

    /**
     * 不脱敏
     */
    OFF(0),
    /**
     * 注解脱敏
     */
    ANNOTATION(1),
    /**
     * 全脱敏（如果方法中存在脱敏注解，则脱敏注解优先）
     */
    FULL(2);

    private Integer value;

}