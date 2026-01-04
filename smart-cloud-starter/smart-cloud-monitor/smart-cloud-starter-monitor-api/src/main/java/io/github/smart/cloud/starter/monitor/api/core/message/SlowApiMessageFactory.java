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
package io.github.smart.cloud.starter.monitor.api.core.message;

import io.github.smart.cloud.monitor.common.dto.wework.AbstractWeworkRobotMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotMarkdownMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotTextMessageDTO;
import io.github.smart.cloud.monitor.common.enums.WeworkRobotMessageType;
import io.github.smart.cloud.starter.monitor.api.core.IMessageFactory;
import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.util.PercentUtil;
import io.github.smart.cloud.utility.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 接口异常消息工厂
 *
 * @author collin.li
 * @date 2025-10-22
 */
public class SlowApiMessageFactory extends AbstractMessageFactory implements IMessageFactory<ApiSlowAlertDTO> {

    public SlowApiMessageFactory(ApiMonitorProperties apiMonitorProperties) {
        super(apiMonitorProperties);
    }

    /**
     * 构建企业微信机器人消息体
     *
     * @param apiSlowAlerts
     * @return
     */
    @Override
    public AbstractWeworkRobotMessageDTO buildSummaryAlertMessages(List<ApiSlowAlertDTO> apiSlowAlerts) {
        if (WeworkRobotMessageType.MARKDOWN == apiMonitorProperties.getSlowApiMonitor().getMessageType()) {
            return buildSummaryAlertMarkdownMessages(apiSlowAlerts);
        } else {
            return buildSummaryAlertTextMessage(apiSlowAlerts);
        }
    }

    /**
     * 构建企业微信机器人消息体（立即通知）
     *
     * @param apiSlowAlert
     * @return
     */
    @Override
    public AbstractWeworkRobotMessageDTO buildImmediateAlertMessage(ApiSlowAlertDTO apiSlowAlert) {
        if (WeworkRobotMessageType.MARKDOWN == apiMonitorProperties.getSlowApiMonitor().getMessageType()) {
            StringBuilder content = new StringBuilder(128);
            content.append("**").append(appName).append("**慢接口如下:")
                    .append("\n**IP**：").append(ip)
                    .append("\n**时间**：").append(DateUtil.getCurrentDateTime())
                    .append("\n\n>**接口**：").append(apiSlowAlert.getName());
            if (apiSlowAlert.getTraceId() != null) {
                content.append("\n>**traceId**：").append(apiSlowAlert.getTraceId());
            }
            content.append("\n>**耗时**：").append(apiSlowAlert.getMaxCost()).append("ms");

            Set<String> reminders = apiMonitorProperties.getSlowApiMonitor().getReminders();
            if (!CollectionUtils.isEmpty(reminders)) {
                content.append("\n\n<@").append(StringUtils.join(reminders, ">\n<@")).append(">");
            }
            return new WeworkRobotMarkdownMessageDTO(content.toString());
        } else {
            StringBuilder content = new StringBuilder(128);
            content.append("【").append(appName).append("】慢接口如下:")
                    .append("\n【IP】：").append(ip)
                    .append("\n【时间】：").append(DateUtil.formatDateTime(new Date()));
            content.append("\n───────────────────────");
            content.append("\n【接口】：").append(apiSlowAlert.getName());
            if (apiSlowAlert.getTraceId() != null) {
                content.append("\n【traceId】：").append(apiSlowAlert.getTraceId());
            }
            content.append("\n【耗时】：").append(apiSlowAlert.getMaxCost()).append("ms");

            return new WeworkRobotTextMessageDTO(content.toString(), apiMonitorProperties.getSlowApiMonitor().getReminders());
        }
    }

    /**
     * 构造企业微信机器人markdown格式消息
     *
     * @param apiSlowAlerts
     * @return
     */
    private AbstractWeworkRobotMessageDTO buildSummaryAlertMarkdownMessages(List<ApiSlowAlertDTO> apiSlowAlerts) {
        StringBuilder content = new StringBuilder(128);
        content.append("**").append(appName).append("** ")
                .append(TimeUnit.SECONDS.toMinutes(apiMonitorProperties.getCleanIntervalSeconds()))
                .append("分钟慢接口统计:")
                .append("\n**IP**：").append(ip);

        boolean needAtSomeone = false;
        for (int i = 0; i < apiSlowAlerts.size(); i++) {
            ApiSlowAlertDTO apiSlowAlert = apiSlowAlerts.get(i);
            content.append("\n\n>**接口").append(i + 1).append("**：").append(apiSlowAlert.getName())
                    .append("\n>**请求总数**：").append(apiSlowAlert.getTotalCount())
                    .append("\n>**慢请求数**：").append(apiSlowAlert.getSlowCount())
                    .append("\n>**慢接口率**：").append(PercentUtil.format(apiSlowAlert.getSlowRate()));
            if (apiSlowAlert.getTraceId() != null) {
                content.append("\n>**traceId**：").append(apiSlowAlert.getTraceId());
            }
            content.append("\n>**最大耗时**：").append(apiSlowAlert.getMaxCost()).append("ms");

            needAtSomeone |= apiSlowAlert.getNeedAtSomeone();
        }

        Set<String> reminders = apiMonitorProperties.getSlowApiMonitor().getReminders();
        if (needAtSomeone && !CollectionUtils.isEmpty(reminders)) {
            content.append("\n\n<@").append(StringUtils.join(reminders, ">\n<@")).append(">");
        }

        return new WeworkRobotMarkdownMessageDTO(content.toString());
    }

    /**
     * 构造企业微信机器人text格式消息
     *
     * @param apiSlowAlerts
     * @return
     */
    private AbstractWeworkRobotMessageDTO buildSummaryAlertTextMessage(List<ApiSlowAlertDTO> apiSlowAlerts) {
        StringBuilder content = new StringBuilder(128);
        content.append("【").append(appName).append("】")
                .append(TimeUnit.SECONDS.toMinutes(apiMonitorProperties.getCleanIntervalSeconds()))
                .append("分钟慢接口统计:")
                .append("\n【IP】：").append(ip);

        boolean needAtSomeone = false;
        int exceptionCount = apiSlowAlerts.size();
        for (int i = 0; i < exceptionCount; i++) {
            ApiSlowAlertDTO apiSlowAlert = apiSlowAlerts.get(i);
            if (exceptionCount > 1) {
                content.append("\n───────────────────────");
            }
            content.append("\n【接口").append(i + 1).append("】：").append(apiSlowAlert.getName())
                    .append("\n【请求总数】：").append(apiSlowAlert.getTotalCount())
                    .append("\n【慢请求数】：").append(apiSlowAlert.getSlowCount())
                    .append("\n【慢接口率】：").append(PercentUtil.format(apiSlowAlert.getSlowRate()));
            if (apiSlowAlert.getTraceId() != null) {
                content.append("\n【traceId】：").append(apiSlowAlert.getTraceId());
            }
            content.append("\n【最大耗时】：").append(apiSlowAlert.getMaxCost()).append("ms");
            needAtSomeone |= apiSlowAlert.getNeedAtSomeone();
        }

        Set<String> mentionedMobileList = needAtSomeone ? apiMonitorProperties.getSlowApiMonitor().getReminders() : null;
        return new WeworkRobotTextMessageDTO(content.toString(), mentionedMobileList);
    }

}