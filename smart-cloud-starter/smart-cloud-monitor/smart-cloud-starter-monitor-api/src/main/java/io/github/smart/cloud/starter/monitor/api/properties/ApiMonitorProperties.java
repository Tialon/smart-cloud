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
import org.apache.commons.lang3.StringUtils;

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
     * 接口监控事件队列大小
     */
    private int apiMonitorEventQueueSize = 4096;

    /**
     * 清理间隔时间（单位：秒）
     */
    private long cleanIntervalSeconds = 60 * 3L;
    /**
     * 默认的机器人key
     */
    private String robotKey;
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

    /**
     * 获取异常接口通知机器人key
     *
     * @return
     */
    public String getExceptionApiRobotKey() {
        if (StringUtils.isBlank(exceptionApiMonitor.getRobotKey())) {
            return robotKey;
        }
        return exceptionApiMonitor.getRobotKey();
    }

    /**
     * 获取慢接口通知机器人key
     *
     * @return
     */
    public String getSlowApiRobotKey() {
        if (StringUtils.isBlank(slowApiMonitor.getRobotKey())) {
            return robotKey;
        }
        return slowApiMonitor.getRobotKey();
    }

}