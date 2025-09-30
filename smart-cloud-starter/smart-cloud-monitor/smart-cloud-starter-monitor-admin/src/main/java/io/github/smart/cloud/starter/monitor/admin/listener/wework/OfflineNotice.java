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
package io.github.smart.cloud.starter.monitor.admin.listener.wework;

import io.github.smart.cloud.monitor.common.WeworkRobotAgent;
import io.github.smart.cloud.monitor.common.dto.wework.AbstractWeworkRobotMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotMarkdownMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotTextMessageDTO;
import io.github.smart.cloud.monitor.common.enums.WeworkRobotMessageType;
import io.github.smart.cloud.starter.monitor.admin.component.ReminderComponent;
import io.github.smart.cloud.starter.monitor.admin.event.notice.OfflineNoticeEvent;
import io.github.smart.cloud.starter.monitor.admin.properties.MonitorProperties;
import io.github.smart.cloud.utility.JacksonUtil;
import org.springframework.util.StringUtils;

/**
 * 在线实例为0时，企业微信通知
 *
 * @author collin
 * @date 2024-02-23
 */
public class OfflineNotice extends AbstractWeworkNotice<OfflineNoticeEvent> {

    public OfflineNotice(WeworkRobotAgent weworkRobotAgent, MonitorProperties monitorProperties, ReminderComponent reminderComponent) {
        super(weworkRobotAgent, monitorProperties, reminderComponent);
    }

    @Override
    public void onApplicationEvent(OfflineNoticeEvent event) {
        String name = event.getName();
        StringBuilder content = new StringBuilder(64);
        content.append("【").append(name).append("】服务在线实例数为0");

        AbstractWeworkRobotMessageDTO messageDto = null;
        if (monitorProperties.getMessageType() == WeworkRobotMessageType.MARKDOWN) {
            String reminders = getReminderParams(name);
            if (StringUtils.hasText(reminders)) {
                content.append(reminders);
            }
            messageDto = new WeworkRobotMarkdownMessageDTO(content.toString());
        } else {
            messageDto = new WeworkRobotTextMessageDTO(content.toString(), getReminders(name));
        }
        String robotMessage = JacksonUtil.toJson(messageDto);
        weworkRobotAgent.sendMessage(monitorProperties.getRobotKey(name), robotMessage);
    }

}