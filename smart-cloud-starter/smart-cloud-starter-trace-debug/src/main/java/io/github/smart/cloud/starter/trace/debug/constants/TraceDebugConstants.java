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
package io.github.smart.cloud.starter.trace.debug.constants;

import io.github.smart.cloud.starter.trace.debug.enums.EnumTraceDebugType;
import io.github.smart.cloud.starter.trace.debug.filter.TraceDebugHttpHeaderArgsParseFilter;

/**
 * 链路调试常量
 *
 * @author collin.li
 * @date 2026-02-27
 */
public class TraceDebugConstants {

    /**
     * 方法耗时打印日志开关请求头
     *
     * @see TraceDebugHttpHeaderArgsParseFilter
     * @see EnumTraceDebugType
     */
    public static final String TRACE_DEBUG_TYPE = "smart-trace-debug-type";

}