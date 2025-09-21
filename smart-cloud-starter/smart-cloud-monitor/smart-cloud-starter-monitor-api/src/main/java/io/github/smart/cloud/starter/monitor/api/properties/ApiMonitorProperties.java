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
package io.github.smart.cloud.starter.monitor.api.properties;

import io.github.smart.cloud.starter.monitor.api.annotation.ApiMonitor;
import lombok.Getter;
import lombok.Setter;

/**
 * 接口健康检测配置属性
 * <p/>
 * <b>配置样例：</b>
 * <pre>
 * smart:
 *   api-monitor:
 *     exception-api-monitor:
 *       unhealthMatchMinCount: 10
 *       defaultFailRateThreshold: 0.3
 *       failRateThresholds:
 *         '[LoginController#login]': 0
 *         '[OrderController#query]': 0
 * </pre>
 *
 * @author collin
 * @date 2024-01-6
 */
@Getter
@Setter
public class ApiMonitorProperties {

    public static final String PREFIX = "smart.api-monitor";

    /**
     * 发送消息时的代理host
     */
    private String proxyHost;
    /**
     * 发送消息时的代理端口
     */
    private int port;
    /**
     * 清理间隔时间（单位：秒）
     */
    private long cleanIntervalSeconds = 60 * 3L;
    /**
     * 接口监控切面是否支持mapping注解
     * <li>true-支持</li>
     * <li>false-不支持，仅支持@ApiMonitor注解</li>
     */
    private boolean pointCutSupportMappingAnnotation = true;
    /**
     * 异常接口监控配置
     */
    private ExceptionApiMonitorProperties exceptionApiMonitor = new ExceptionApiMonitorProperties();
    /**
     * 慢接口监控配置
     */
    private SlowApiMonitorProperties slowApiMonitor = new SlowApiMonitorProperties();

}