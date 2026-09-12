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
package io.github.smart.cloud.starter.monitor.api.core;

import io.github.smart.cloud.monitor.common.dto.wework.AbstractWeworkRobotMessageDTO;
import io.github.smart.cloud.starter.monitor.api.dto.ApiAlertCommonDTO;

import java.util.List;

/**
 * 消息工厂
 *
 * @param <T>
 * @author collin.li
 * @date 2025-10-25
 */
public interface IMessageFactory<T extends ApiAlertCommonDTO> {

    /**
     * 构建企业微信机器人消息体（汇总通知）
     *
     * @param apiExceptions
     * @return
     */
    AbstractWeworkRobotMessageDTO buildSummaryAlertMessages(List<T> apiExceptions);

    /**
     * 构建企业微信机器人消息体（立即通知）
     *
     * @param apiExceptionAlert
     * @return
     */
    AbstractWeworkRobotMessageDTO buildImmediateAlertMessage(T apiExceptionAlert);

}