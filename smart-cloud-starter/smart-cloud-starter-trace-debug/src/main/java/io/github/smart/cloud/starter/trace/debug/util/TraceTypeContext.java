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
package io.github.smart.cloud.starter.trace.debug.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.smart.cloud.starter.trace.debug.enums.EnumTraceDebugType;

/**
 * 跟踪上下文标记
 *
 * @author collin.li
 * @date 2026-01-09
 */
public class TraceTypeContext {

    /**
     * 存储当前线程的跟踪类型
     */
    public static final TransmittableThreadLocal<EnumTraceDebugType> CONTEXT = new TransmittableThreadLocal<>();

    /**
     * 设置当前线程的跟踪类型
     *
     * @param traceType
     */
    public static void set(EnumTraceDebugType traceType) {
        CONTEXT.set(traceType);
    }

    /**
     * 获取当前线程的跟踪类型
     *
     * @return
     */
    public static EnumTraceDebugType get() {
        return CONTEXT.get();
    }

    /**
     * 移除当前线程的跟踪类型，避免内存泄漏
     */
    public static void remove() {
        CONTEXT.remove();
    }

}