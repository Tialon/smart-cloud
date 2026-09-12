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
import io.github.smart.cloud.starter.monitor.api.dto.ApiSlowAlertDTO;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;

/**
 * 慢接口企业微信告警通知
 *
 * @author collin
 * @date 2024-07-01
 */
public class SlowAbstractApiMonitorWeworkAlertListener extends AbstractApiMonitorWeworkAlertListener<ApiSlowAlertDTO> {

    public SlowAbstractApiMonitorWeworkAlertListener(WeworkRobotAgent weworkRobotAgent, ApiMonitorProperties apiMonitorProperties, IMessageFactory<ApiSlowAlertDTO> messageFactory) {
        super(weworkRobotAgent, apiMonitorProperties, messageFactory);
    }

    @Override
    protected String getRobotKey() {
        return apiMonitorProperties.getSlowApiRobotKey();
    }

}