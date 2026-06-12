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
package io.github.smart.cloud.design.pattern.utility.chain;

/**
 * 责任链模式处理器接口
 *
 * @param <T> 处理的数据类型
 * @author collin.li
 * @date 2025-10-11
 */
@FunctionalInterface
public interface Handler<T> {

    /**
     * 处理请求
     *
     * @param context 上下文对象
     * @return 是否继续处理下一个处理器
     */
    HandlerResult handle(T context);

}