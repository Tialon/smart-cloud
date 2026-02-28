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
package io.github.smart.cloud.starter.trace.debug.intercept;

import io.github.smart.cloud.starter.trace.debug.enums.EnumTraceDebugType;
import io.github.smart.cloud.starter.trace.debug.util.TraceTypeContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 请求跟踪切面
 *
 * @author collin.li
 * @date 2025-12-03
 */
public class TraceDebugInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        EnumTraceDebugType traceType = TraceTypeContext.get();
        if (traceType == null) {
            return invocation.proceed();
        }

        return traceType.apply(invocation);
    }

}