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

import io.github.smart.cloud.monitor.common.WeworkRobotAgent;
import io.github.smart.cloud.starter.monitor.api.core.IMessageFactory;
import io.github.smart.cloud.starter.monitor.api.dto.ApiAlertCommonDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorAlertEvent;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;

/**
 * 接口异常企业微信告警通知
 *
 * @author collin
 * @date 2024-07-01
 */
@RequiredArgsConstructor
public abstract class AbstractApiMonitorWeworkAlertListener<T extends ApiAlertCommonDTO> implements ApplicationListener<ApiMonitorAlertEvent<T>> {

    private final WeworkRobotAgent weworkRobotAgent;
    protected final ApiMonitorProperties apiMonitorProperties;
    private final IMessageFactory<T> messageFactory;

    @Override
    public void onApplicationEvent(ApiMonitorAlertEvent<T> event) {
        if (event.isImmediateAlert()) {
            weworkRobotAgent.sendMessage(getRobotKey(), messageFactory.buildImmediateAlertMessage(event.getAlertInfos().get(0)));
        } else {
            weworkRobotAgent.sendMessage(getRobotKey(), messageFactory.buildSummaryAlertMessages(event.getAlertInfos()));
        }
    }

    /**
     * 获取企业微信机器人key
     *
     * @return
     */
    protected abstract String getRobotKey();

}