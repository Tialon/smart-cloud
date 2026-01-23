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
package io.github.smart.cloud.starter.monitor.api.event;

import io.github.smart.cloud.starter.monitor.api.enums.MonitorType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * 接口监控事件
 *
 * @author collin
 * @date 2025-08-30
 */
@Getter
@Setter
public class ApiMonitorEvent extends ApplicationEvent {

    /**
     * 接口名
     */
    private String apiName;
    /**
     * 接口耗时（单位：毫秒）
     */
    private long cost;
    /**
     * 接口异常信息
     */
    private Throwable throwable;
    /**
     * 链路号
     */
    private String traceId;
    /**
     * 监控类型
     */
    private MonitorType monitorType;

    public ApiMonitorEvent(Object source) {
        super(source);
    }

}