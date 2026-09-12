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

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 责任链工厂
 *
 * @param <T> 数据类型
 * @author collin.li
 * @date 2025-10-11
 */
@Slf4j
public class HandlerChainFactory<T> {

    private HandlerChainFactory() {
    }

    public static <T> HandlerChain<T> create(List<? extends Handler<T>> handlers) {
        return new HandlerChain<>(handlers);
    }

    public static <T> HandlerChain<T> sortAndCreate(List<OrderedHandler<T>> handlers) {
        Collections.sort(handlers, Comparator.comparingInt(OrderedHandler::getOrder));
        return create(handlers);
    }

}