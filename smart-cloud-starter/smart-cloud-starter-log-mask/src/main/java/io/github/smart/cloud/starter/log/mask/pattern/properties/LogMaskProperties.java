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