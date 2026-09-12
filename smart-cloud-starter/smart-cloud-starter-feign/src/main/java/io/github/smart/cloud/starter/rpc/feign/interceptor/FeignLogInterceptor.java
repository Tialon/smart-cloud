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
package io.github.smart.cloud.starter.rpc.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.github.smart.cloud.common.web.util.WebUtil;
import io.github.smart.cloud.constants.OrderConstant;
import io.github.smart.cloud.constants.SymbolConstant;
import io.github.smart.cloud.starter.configure.properties.FeignLogProperties;
import io.github.smart.cloud.starter.configure.properties.SmartProperties;
import io.github.smart.cloud.starter.rpc.feign.pojo.FeignLogAspectDTO;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

/**
 * feign切面
 *
 * @author collin
 * @date 2019-04-21
 */
@Slf4j
@RequiredArgsConstructor
public class FeignLogInterceptor implements MethodInterceptor, RequestInterceptor, Ordered {

    private final SmartProperties smartProperties;
    /**
     * 默认日志最大长度
     */
    private static final int DEFAULT_LOG_MAX_LENGTH = 2048;
    private static final ThreadLocal<Map<String, Collection<String>>> FEIGN_HEADER_THREAD_LOCAL = new ThreadLocal<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = null;
        try {
            result = invocation.proceed();

            if (log.isWarnEnabled()) {
                FeignLogProperties feignLogProperties = smartProperties.getFeign().getLog();
                long cost = System.currentTimeMillis() - startTime;
                if (cost >= feignLogProperties.getSlowApiMinCost()) {
                    log.warn("rpc.slow=>{}", truncate(buildFeignLogAspectDO(invocation.getMethod(), invocation.getArguments(), result, cost), feignLogProperties.getLogMaxLength()));
                } else {
                    log.info("rpc.info=>{}", truncate(buildFeignLogAspectDO(invocation.getMethod(), invocation.getArguments(), result, cost), feignLogProperties.getLogMaxLength()));
                }
            }
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - startTime;
            FeignLogProperties feignLogProperties = smartProperties.getFeign().getLog();
            log.error("rpc.error=>{}", truncate(buildFeignLogAspectDO(invocation.getMethod(), invocation.getArguments(), result, cost), feignLogProperties.getLogMaxLength()), e);
            throw e;
        } finally {
            // 方法调用顺序：apply（初始化值） ——> invoke（获取值，并清除）
            // 3、防止内存泄漏
            FEIGN_HEADER_THREAD_LOCAL.remove();
        }

        return result;
    }

    /**
     * 日志超长截取
     *
     * @param feignLogAspectDTO
     * @param logMaxLength
     * @return
     */
    private String truncate(FeignLogAspectDTO feignLogAspectDTO, Integer logMaxLength) {
        logMaxLength = logMaxLength == null ? DEFAULT_LOG_MAX_LENGTH : logMaxLength;
        String content = JacksonUtil.toJson(feignLogAspectDTO);
        return StringUtils.truncate(content, logMaxLength);
    }

    private FeignLogAspectDTO buildFeignLogAspectDO(Method method, Object[] args, Object result, long cost) {
        String classMethod = method.getDeclaringClass().getSimpleName() + SymbolConstant.DOT + method.getName();

        FeignLogAspectDTO logDO = new FeignLogAspectDTO();
        logDO.setClassMethod(classMethod);
        logDO.setParams(WebUtil.getRequestArgs(args));
        logDO.setResult(result);
        logDO.setHeaders(FEIGN_HEADER_THREAD_LOCAL.get());
        logDO.setCost(cost);
        return logDO;
    }

    @Override
    public void apply(RequestTemplate template) {
        FEIGN_HEADER_THREAD_LOCAL.set(template.headers());
    }

    @Override
    public int getOrder() {
        return OrderConstant.FEIGN_LOG;
    }

}