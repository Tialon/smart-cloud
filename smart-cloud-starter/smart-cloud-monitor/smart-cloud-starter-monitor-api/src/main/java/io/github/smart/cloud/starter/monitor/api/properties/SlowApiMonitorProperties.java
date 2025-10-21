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

import io.github.smart.cloud.monitor.common.enums.WeworkRobotMessageType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.*;

/**
 * 慢接口监控配置属性
 *
 * @author collin
 * @date 2024-01-6
 */
@Getter
@Setter
public class SlowApiMonitorProperties {

    /**
     * 接口白名单（不监听）
     */
    private Set<String> apiWhiteList = new HashSet<>();
    /**
     * 慢接口最大上报数
     */
    private int apiReportMaxCount = 10;
    /**
     * 慢接口告警@某人提醒默认阈值，单位：毫秒
     */
    private long atSomeoneCostThreshold = 10000L;
    /**
     * 慢接口默认阈值，单位：毫秒
     */
    private long defaultCostThreshold = 3000L;
    /**
     * 慢接口阈值，单位：毫秒
     * <p>
     * <接口名, 慢接口阈值>
     */
    private Map<String, Long> apiCostThresholds = new HashMap<>();

    /**
     * 默认慢接口率阈值（默认无限制）
     */
    private BigDecimal defaultSlowRateThreshold = BigDecimal.ZERO;
    /**
     * 特定接口慢接口率阈值
     */
    private Map<String, BigDecimal> slowRateThresholds = new HashMap<>();
    // -------企业微信通知配置 start
    /**
     * 慢接口通知间隔时间（单位：秒）
     */
    private long slowApiNoticeIntervalSeconds = 60L;
    /**
     * 企业微信机器人key
     */
    private String robotKey;
    /**
     * 消息类型
     */
    private WeworkRobotMessageType messageType = WeworkRobotMessageType.MARKDOWN;
    /**
     * 异常提醒人（markdown类型消息为userid；text类型消息为手机号）
     */
    private Set<String> reminders = new LinkedHashSet<>();
    // -------企业微信通知配置 end

    /**
     * 获取接口阈值
     *
     * @param apiName
     * @return
     */
    public long getCostThreshold(String apiName) {
        return apiCostThresholds.getOrDefault(apiName, defaultCostThreshold);
    }

    /**
     * 慢接口率阈值
     *
     * @param apiName
     * @return
     */
    public BigDecimal getSlowRateThreshold(String apiName) {
        return slowRateThresholds.getOrDefault(apiName, defaultSlowRateThreshold);
    }

}