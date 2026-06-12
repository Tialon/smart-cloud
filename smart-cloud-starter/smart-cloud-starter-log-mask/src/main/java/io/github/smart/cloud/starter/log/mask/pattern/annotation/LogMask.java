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