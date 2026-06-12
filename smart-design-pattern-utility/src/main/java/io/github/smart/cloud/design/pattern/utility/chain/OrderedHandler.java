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

import org.springframework.core.Ordered;

/**
 * 带执行顺序（值小的优先执行）的责任链模式处理器接口
 *
 * @param <T> 处理的数据类型
 * @author collin.li
 * @date 2025-10-11
 */
public interface OrderedHandler<T> extends Handler<T>, Ordered {

}