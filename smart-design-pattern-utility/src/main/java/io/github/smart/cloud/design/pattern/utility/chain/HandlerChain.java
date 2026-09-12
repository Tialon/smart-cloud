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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 责任链处理者
 *
 * @author collin.li
 * @date 2025-10-11
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HandlerChain<T> {

    /**
     * 责任链待处理集合
     */
    private final List<? extends Handler<T>> handlers;

    /**
     * 执行责任链
     *
     * @param data
     * @return
     */
    public HandlerResult execute(T data) {
        for (Handler<T> handler : handlers) {
            HandlerResult handlerResult = handler.handle(data);
            if (!handlerResult.isPass()) {
                log.warn("{} not pass", handler.getClass().getSimpleName());
                return handlerResult;
            }
        }

        return HandlerResult.success();
    }

}