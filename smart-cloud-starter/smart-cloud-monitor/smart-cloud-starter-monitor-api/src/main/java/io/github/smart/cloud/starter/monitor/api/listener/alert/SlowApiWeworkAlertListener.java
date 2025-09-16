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
package io.github.smart.cloud.starter.monitor.api.listener.alert;

import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotMarkdownMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotTextMessageDTO;
import io.github.smart.cloud.monitor.common.enums.WeworkRobotMessageType;
import io.github.smart.cloud.starter.monitor.api.component.WeworkRobotComponent;
import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.event.SlowApiAlertEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.SlowApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.util.PercentUtil;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 接口异常企业微信告警通知
 *
 * @author collin
 * @date 2024-07-01
 */
@RequiredArgsConstructor
public class SlowApiWeworkAlertListener extends AbstractWeworkAlertListener implements ApplicationListener<SlowApiAlertEvent> {

    private final WeworkRobotComponent weworkRobotComponent;
    private final ApiMonitorProperties apiMonitorProperties;

    @Override
    public void onApplicationEvent(SlowApiAlertEvent event) {
        String robotKey = apiMonitorProperties.getExceptionApiMonitor().getRobotKey();
        weworkRobotComponent.sendWeworkRobotMessage(robotKey, buildWeworkRobotMessage(event.getSlowApiAlerts()));
    }

    /**
     * 构建企业微信机器人消息体
     *
     * @param apiSlowAlerts
     * @return
     */
    public String buildWeworkRobotMessage(List<ApiSlowAlertDTO> apiSlowAlerts) {
        if (WeworkRobotMessageType.MARKDOWN == apiMonitorProperties.getSlowApiMonitor().getMessageType()) {
            return buildWeworkRobotMarkdownMessage(apiSlowAlerts);
        } else {
            return buildWeworkRobotTextMessage(apiSlowAlerts);
        }
    }

    /**
     * 构造企业微信机器人markdown格式消息
     *
     * @param apiSlowAlerts
     * @return
     */
    private String buildWeworkRobotMarkdownMessage(List<ApiSlowAlertDTO> apiSlowAlerts) {
        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        StringBuilder content = new StringBuilder(128);
        content.append("**").append(appName).append("** ")
                .append(TimeUnit.SECONDS.toMinutes(slowApiMonitorProperties.getCleanIntervalSeconds()))
                .append("分钟慢接口统计:")
                .append("\n**IP**：").append(ip);

        for (int i = 0; i < apiSlowAlerts.size(); i++) {
            ApiSlowAlertDTO apiSlowAlert = apiSlowAlerts.get(i);
            content.append("\n\n>**接口").append(i + 1).append("**：").append(apiSlowAlert.getName())
                    .append("\n>**请求总数**：").append(apiSlowAlert.getTotalCount())
                    .append("\n>**慢请求数**：").append(apiSlowAlert.getSlowCount())
                    .append("\n>**慢接口率**：").append(PercentUtil.format(apiSlowAlert.getSlowRate()))
                    .append("\n>**最大耗时（ms）**：").append(apiSlowAlert.getMaxCost());
        }

        if (!CollectionUtils.isEmpty(slowApiMonitorProperties.getReminders())) {
            content.append("\n\n<@").append(StringUtils.join(slowApiMonitorProperties.getReminders(), ">\n<@")).append(">");
        }

        return JacksonUtil.toJson(new WeworkRobotMarkdownMessageDTO(content.toString()));
    }

    /**
     * 构造企业微信机器人text格式消息
     *
     * @param apiSlowAlerts
     * @return
     */
    private String buildWeworkRobotTextMessage(List<ApiSlowAlertDTO> apiSlowAlerts) {
        SlowApiMonitorProperties slowApiMonitorProperties = apiMonitorProperties.getSlowApiMonitor();
        StringBuilder content = new StringBuilder(128);
        content.append("【").append(appName).append("】")
                .append(TimeUnit.SECONDS.toMinutes(slowApiMonitorProperties.getCleanIntervalSeconds()))
                .append("分钟慢接口统计:")
                .append("\n【IP】：").append(ip);

        int exceptionCount = apiSlowAlerts.size();
        for (int i = 0; i < exceptionCount; i++) {
            ApiSlowAlertDTO apiSlowAlert = apiSlowAlerts.get(i);
            if (exceptionCount > 1) {
                content.append("\n───────────────────────");
            }
            content.append("\n【接口").append(i + 1).append("】：").append(apiSlowAlert.getName())
                    .append("\n【请求总数】：").append(apiSlowAlert.getTotalCount())
                    .append("\n【慢请求数】：").append(apiSlowAlert.getSlowCount())
                    .append("\n【慢接口率】：").append(PercentUtil.format(apiSlowAlert.getSlowRate()))
                    .append("\n【最大耗时（ms）】：").append(apiSlowAlert.getMaxCost());
        }

        return JacksonUtil.toJson(new WeworkRobotTextMessageDTO(content.toString(), slowApiMonitorProperties.getReminders()));
    }

}