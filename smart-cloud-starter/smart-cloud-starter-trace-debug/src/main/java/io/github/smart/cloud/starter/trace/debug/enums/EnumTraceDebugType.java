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
package io.github.smart.cloud.starter.trace.debug.enums;

import io.github.smart.cloud.starter.trace.debug.util.TraceCostContext;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * 请求链路跟踪调试类型
 *
 * @author collin.li
 * @date 2025-12-29
 */
@Slf4j
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum EnumTraceDebugType {

    /**
     * 仅打印耗时
     */
    ONLY_COST("10") {
        @Override
        public Object apply(MethodInvocation invocation) throws Throwable {
            long startTs = System.nanoTime();
            Object result = invocation.proceed();
            long endTs = System.nanoTime();
            Method method = invocation.getMethod();
            long cost = endTs - startTs;
            log.info("trace==>{}#{} costType{} {}ns", method.getDeclaringClass().getSimpleName(), method.getName(), getCostType(cost), cost);
            return result;
        }
    },
    /**
     * 打印耗时汇总数据
     */
    COST_SUMMARY("11") {
        @Override
        public Object apply(MethodInvocation invocation) throws Throwable {
            long startTs = System.nanoTime();
            Object result = invocation.proceed();
            long endTs = System.nanoTime();
            Method method = invocation.getMethod();
            long cost = endTs - startTs;
            String methodName = method.getDeclaringClass().getSimpleName() + "#" + method.getName();
            log.info("trace==>{} costType{} {}ns", methodName, getCostType(cost), cost);
            TraceCostContext.add(methodName, cost);
            return result;
        }
    },
    /**
     * 打印入参、出差
     */
    ONLY_INPUT_OUTPUT("20") {
        @Override
        public Object apply(MethodInvocation invocation) throws Throwable {
            if (notMatch(invocation.getThis())) {
                return invocation.proceed();
            }

            Object result = invocation.proceed();
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();
            log.info("trace==>{}#{} args={}, result={}", method.getDeclaringClass().getSimpleName(),
                    method.getName(), JacksonUtil.toJson(args), JacksonUtil.toJson(result));
            return result;
        }
    },
    /**
     * 打印入参、出差、耗时
     */
    ALL("30") {
        @Override
        public Object apply(MethodInvocation invocation) throws Throwable {
            if (notMatch(invocation.getThis())) {
                return invocation.proceed();
            }

            long startTs = System.currentTimeMillis();
            Object result = invocation.proceed();
            long endTs = System.currentTimeMillis();
            Object[] args = invocation.getArguments();
            Method method = invocation.getMethod();
            long cost = endTs - startTs;
            log.info("trace==>{}#{} cost={}ms, args={}, result={}", method.getDeclaringClass().getSimpleName(),
                    method.getName(), cost, JacksonUtil.toJson(args), JacksonUtil.toJson(result));

            return result;
        }
    };

    private String value;

    /**
     * 接口耗时（单位：纳秒，即0.5ms、1ms、5ms、10ms、50ms、100ms、200ms、500ms、1s）
     */
    private static final long[] COST_RANGES = {
            // 0.5ms
            500000,
            // 1ms
            1000000,
            // 5ms
            1000000 * 5,
            // 10ms
            1000000 * 10,
            // 50ms
            1000000 * 50,
            // 100ms
            1000000 * 100,
            // 200ms
            1000000 * 200,
            // 500ms
            1000000 * 500,
            // 1s
            1000000 * 1000};

    /**
     * 跟踪处理逻辑
     *
     * @param invocation
     * @return
     * @throws Throwable
     */
    public abstract Object apply(MethodInvocation invocation) throws Throwable;

    /**
     * 类型解析
     *
     * @param type
     * @return
     */
    public static EnumTraceDebugType parse(String type) {
        for (EnumTraceDebugType traceType : values()) {
            if (traceType.value.equals(type)) {
                return traceType;
            }
        }

        return null;
    }

    /**
     * 获取耗时类型
     *
     * @param cost
     * @return
     */
    private static int getCostType(long cost) {
        for (int i = 0; i < COST_RANGES.length; i++) {
            if (cost < COST_RANGES[i]) {
                return i;
            }
        }

        return COST_RANGES.length;
    }

    /**
     * 是否不匹配（不打印出入参）
     *
     * @param proxy
     * @return
     */
    private static boolean notMatch(Object proxy) {
        String simipleName = proxy.getClass().getSimpleName();
        return "Redisson".equals(simipleName)
                || "RedisOperations".equals(simipleName)
                || "RedisZSetAgent".equals(simipleName)
                || "RedisTemplate".equals(simipleName)
                || "StringRedisTemplate".equals(simipleName);
    }

}