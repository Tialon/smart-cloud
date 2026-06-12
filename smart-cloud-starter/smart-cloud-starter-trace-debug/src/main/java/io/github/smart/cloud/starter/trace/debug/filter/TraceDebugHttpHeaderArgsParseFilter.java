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
package io.github.smart.cloud.starter.trace.debug.filter;

import io.github.smart.cloud.starter.trace.debug.constants.TraceDebugConstants;
import io.github.smart.cloud.starter.trace.debug.enums.EnumTraceDebugType;
import io.github.smart.cloud.starter.trace.debug.properties.TraceDebugProperties;
import io.github.smart.cloud.starter.trace.debug.util.TraceTypeContext;
import io.github.smart.cloud.starter.trace.debug.util.TraceCostContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 请求跟踪参数解析
 *
 * @author collin.li
 * @date 2025-12-03
 */
@Slf4j
@RequiredArgsConstructor
public class TraceDebugHttpHeaderArgsParseFilter extends OncePerRequestFilter {

    private final TraceDebugProperties traceDebugProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!traceDebugProperties.isOpen()) {
            filterChain.doFilter(request, response);
            return;
        }

        String traceDebugType = request.getHeader(TraceDebugConstants.TRACE_DEBUG_TYPE);
        if (null == traceDebugType) {
            filterChain.doFilter(request, response);
            return;
        }

        EnumTraceDebugType enumTraceDebugType = EnumTraceDebugType.parse(traceDebugType);
        if (null == enumTraceDebugType) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            TraceTypeContext.set(enumTraceDebugType);
            if (EnumTraceDebugType.COST_SUMMARY == enumTraceDebugType) {
                TraceCostContext.init();
            }

            filterChain.doFilter(request, response);
        } finally {
            try {
                TraceTypeContext.remove();
                if (EnumTraceDebugType.COST_SUMMARY == enumTraceDebugType) {
                    TraceCostContext.printCostSummary();
                    TraceCostContext.remove();
                }
            } catch (Exception e) {
                log.warn("TraceArgsParseFilter finally error", e);
            }
        }
    }

}