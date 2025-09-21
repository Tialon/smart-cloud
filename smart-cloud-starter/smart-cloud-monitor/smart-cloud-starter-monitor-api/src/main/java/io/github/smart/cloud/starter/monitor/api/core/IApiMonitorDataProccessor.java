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

import io.github.smart.cloud.starter.monitor.api.dto.ApiAlertCommonDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorEvent;

import java.util.List;

/**
 * 接口监控数据处理器
 *
 * @param <T>
 * @author collin.li
 * @datge 2025-09-19
 */
public interface IApiMonitorDataProccessor<T extends ApiAlertCommonDTO> {

    /**
     * 设置接口访问信息
     *
     * @param event
     */
    void process(ApiMonitorEvent event);

    /**
     * 查询需要告警的接口信息
     *
     * @return
     */
    List<T> getAlertRecords();

}