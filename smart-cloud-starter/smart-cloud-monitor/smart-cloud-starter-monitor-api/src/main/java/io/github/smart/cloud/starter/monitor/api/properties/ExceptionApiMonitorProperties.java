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
 * 异常接口监控配置属性
 *
 * @author collin
 * @date 2024-01-6
 */
@Getter
@Setter
public class ExceptionApiMonitorProperties {

    /**
     * 异常匹配最小数量
     */
    private int matchMinCount = 5;
    /**
     * 异常接口最大上报数
     */
    private int apiReportMaxCount = 10;
    /**
     * 默认失败阈值（默认0.5）
     */
    private BigDecimal defaultFailRateThreshold = BigDecimal.valueOf(0.5);
    /**
     * 特定接口失败阈值
     */
    private Map<String, BigDecimal> failRateThresholds = new HashMap<>();
    /**
     * 接口白名单（不监听异常）
     */
    private Set<String> apiWhiteList = new HashSet<>();

    // -------企业微信通知配置 start
    /**
     * 异常接口通知间隔时间（单位：秒）
     */
    private long noticeIntervalSeconds = 60L;
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

    /**
     * “需要提醒的异常类名列表”中命中时，需要提醒
     */
    private Boolean alertExceptionMarked = true;
    /**
     * 需要提醒的异常类名列表
     */
    private Set<String> needAlertExceptionClassNames = new HashSet<>();
    /**
     * 需要提醒的异常码列表
     */
    private Set<String> needAlertExceptionCodes = new HashSet<>();

    /**
     * 连续失败熔断告警默认阈值（连续失败多少次触发紧急告警，默认5次）
     */
    private int consecutiveFailThreshold = 5;
    /**
     * 特定接口连续失败熔断告警阈值
     * <p>
     * <接口名, 连续失败阈值>
     * </p>
     */
    private Map<String, Integer> consecutiveFailThresholds = new HashMap<>();
    // -------企业微信通知配置 end

    /**
     * 获取接口连续失败熔断告警阈值
     *
     * @param apiName
     * @return
     */
    public int getConsecutiveFailThreshold(String apiName) {
        return consecutiveFailThresholds.getOrDefault(apiName, consecutiveFailThreshold);
    }

}