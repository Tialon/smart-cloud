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
package io.github.smart.cloud.starter.trace.debug.properties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * 方法链路切面点配置
 * <p><b>注意：</b>切面配置{@link TraceDebugPointCutProperties}不能用<code>@RefreshScope</code>
 * 修饰，否则启动时会报bean循环引用。故要与开关配置{@link TraceDebugProperties}分开管理
 *
 * @author collin.li
 * @date 2025-12-03
 */
@Getter
@Setter
@ToString
public class TraceDebugPointCutProperties {

    /**
     * 要排除的不匹配的类集合（类名全名），如：
     * <p>
     * org.springframework.cloud.context.properties.ConfigurationPropertiesBeans<br>
     * javax.servlet.Filter
     */
    private Set<String> excludeClassSet = new HashSet<>();

    /**
     * 匹配的类集合（类名全名），如：
     * <p>
     * org.springframework.data.redis.core.RedisTemplate<br>
     * org.springframework.data.redis.core.StringRedisTemplate<br>
     * org.redisson.Redisson<br>
     * org.springframework.amqp.rabbit.core.RabbitTemplate
     */
    private Set<String> matchClassSet = new HashSet<>();

}