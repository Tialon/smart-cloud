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
import io.github.smart.cloud.starter.monitor.api.core.WeworkRobotComponent;
import io.github.smart.cloud.starter.monitor.api.dto.ApiExceptionAlertDTO;
import io.github.smart.cloud.starter.monitor.api.enums.ApiExceptionRemindType;
import io.github.smart.cloud.starter.monitor.api.event.ApiExceptionAlertEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.starter.monitor.api.properties.ExceptionApiMonitorProperties;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 接口异常企业微信告警通知
 *
 * @author collin
 * @date 2024-07-01
 */
@RequiredArgsConstructor
public class ApiExceptionWeworkAlertListener extends AbstractWeworkAlertListener implements ApplicationListener<ApiExceptionAlertEvent> {

    private final WeworkRobotComponent weworkRobotComponent;
    private final ApiMonitorProperties apiMonitorProperties;

    @Override
    public void onApplicationEvent(ApiExceptionAlertEvent event) {
        String robotKey = apiMonitorProperties.getExceptionApiMonitor().getRobotKey();
        weworkRobotComponent.sendWeworkRobotMessage(robotKey, buildWeworkRobotMessage(event.getApiExceptions()));
    }

    /**
     * 构建企业微信机器人消息体
     *
     * @param apiExceptions
     * @return
     */
    public String buildWeworkRobotMessage(List<ApiExceptionAlertDTO> apiExceptions) {
        if (WeworkRobotMessageType.MARKDOWN == apiMonitorProperties.getExceptionApiMonitor().getMessageType()) {
            return buildWeworkRobotMarkdownMessage(apiExceptions);
        } else {
            return buildWeworkRobotTextMessage(apiExceptions);
        }
    }

    /**
     * 构造企业微信机器人markdown格式消息
     *
     * @param apiExceptions
     * @return
     */
    private String buildWeworkRobotMarkdownMessage(List<ApiExceptionAlertDTO> apiExceptions) {
        ExceptionApiMonitorProperties exceptionApiMonitor = apiMonitorProperties.getExceptionApiMonitor();
        StringBuilder content = new StringBuilder(128);
        content.append("**risk-service** 3分钟异常接口统计:")
                .append("\n**IP**：").append(ip);
        boolean needMention = false;

        for (int i = 0; i < apiExceptions.size(); i++) {
            ApiExceptionAlertDTO apiException = apiExceptions.get(i);
            boolean isFailRateRemindType = apiException.getRemindType() == ApiExceptionRemindType.FAIL_RATE;

            content.append("\n\n>**接口").append(i + 1).append("**：").append(apiException.getName())
                    .append("\n>**请求总数**：").append(apiException.getTotalCount())
                    .append("\n>**失败数**：").append(apiException.getFailCount())
                    .append("\n>**失败率**：")
                    .append(isFailRateRemindType ? "<font color=\"warning\">" : StringUtils.EMPTY)
                    .append(apiException.getFailRate())
                    .append(isFailRateRemindType ? "</font>" : StringUtils.EMPTY);

            if (apiException.getThrowable() != null) {
                boolean isExceptionRemindType = apiException.getRemindType() == ApiExceptionRemindType.EXCEPTION_INFO
                        || apiException.getRemindType() == ApiExceptionRemindType.FAIL_RATE;
                needMention |= isExceptionRemindType;

                content.append("\n>**异常信息**：")
                        .append(isExceptionRemindType ? "<font color=\"warning\">" : StringUtils.EMPTY)
                        .append(apiException.getThrowable().toString())
                        .append(isExceptionRemindType ? "</font>" : StringUtils.EMPTY);
            }
        }

        if (needMention && !CollectionUtils.isEmpty(exceptionApiMonitor.getReminders())) {
            content.append("\n\n<@").append(StringUtils.join(exceptionApiMonitor.getReminders(), ">\n<@")).append(">");
        }

        return JacksonUtil.toJson(new WeworkRobotMarkdownMessageDTO(content.toString()));
    }

    /**
     * 构造企业微信机器人text格式消息
     *
     * @param apiExceptions
     * @return
     */
    private String buildWeworkRobotTextMessage(List<ApiExceptionAlertDTO> apiExceptions) {
        StringBuilder content = new StringBuilder(128);
        content.append("【").append(appName).append("】")
                .append(TimeUnit.SECONDS.toMinutes(apiMonitorProperties.getCleanIntervalSeconds()))
                .append("分钟异常接口统计:")
                .append("\n【IP】：").append(ip);

        boolean needMention = false;
        int exceptionCount = apiExceptions.size();
        for (int i = 0; i < exceptionCount; i++) {
            ApiExceptionAlertDTO apiException = apiExceptions.get(i);
            boolean isFailRateRemindType = apiException.getRemindType() == ApiExceptionRemindType.FAIL_RATE;
            if (exceptionCount > 1) {
                content.append("\n───────────────────────");
            }
            content.append("\n【接口").append(i + 1).append("】：").append(apiException.getName())
                    .append("\n【请求总数】：").append(apiException.getTotalCount())
                    .append("\n【失败数】：").append(apiException.getFailCount())
                    .append("\n").append(isFailRateRemindType ? "⚠" : StringUtils.EMPTY).append("【失败率】：").append(apiException.getFailRate());

            if (apiException.getThrowable() != null) {
                boolean isExceptionRemindType = apiException.getRemindType() == ApiExceptionRemindType.EXCEPTION_INFO;
                needMention |= isExceptionRemindType;
                content.append("\n").append(isExceptionRemindType ? "⚠" : StringUtils.EMPTY).append("【异常信息】：").append(apiException.getThrowable().toString());
            }
        }

        Set<String> mentionedMobileList = needMention ? apiMonitorProperties.getExceptionApiMonitor().getReminders() : null;
        return JacksonUtil.toJson(new WeworkRobotTextMessageDTO(content.toString(), mentionedMobileList));
    }

}