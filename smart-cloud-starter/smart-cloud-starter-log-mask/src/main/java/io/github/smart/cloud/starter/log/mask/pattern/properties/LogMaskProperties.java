package io.github.smart.cloud.starter.log.mask.pattern.properties;

import io.github.smart.cloud.starter.log.mask.pattern.PatternConfig;
import io.github.smart.cloud.starter.log.mask.pattern.enums.MaskMode;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

/**
 * 日志脱敏配置
 *
 * @author collin.li
 * @date 2025-12-24
 */
@Slf4j
@Getter
@Setter
public class LogMaskProperties implements InitializingBean, ApplicationListener<RefreshScopeRefreshedEvent> {

    /**
     * 脱敏模式
     *
     * @see MaskMode
     */
    private Integer mode;
    /**
     * 正则匹配的字段名
     */
    private Set<String> fieldNameSet = new HashSet<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        PatternConfig.setMode(mode);
        PatternConfig.resetMaskFieldSet(fieldNameSet);
        log.info("mask config|mode={}, fieldNameSet={}", PatternConfig.getMode(), JacksonUtil.toJson(PatternConfig.getFieldNameSet()));
    }

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        // 处理“@RefreshScope刷新时，afterPropertiesSet不执行”的问题
        // do nothing
    }
}