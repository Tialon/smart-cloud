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
package io.github.smart.cloud.starter.monitor.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 接口告警异常公共信息
 *
 * @author collin
 * @date 2024-01-6
 */
@Getter
@Setter
@ToString
public class ApiAlertCommonDTO {

    /**
     * 接口名（类名#方法名）
     */
    private String name;
    /**
     * 总访问数
     */
    private Long totalCount;
    /**
     * 链路号
     */
    private String traceId;
    /**
     * 跨度号
     */
    private String spanId;
    /**
     * 是否需要@提醒
     */
    private Boolean needAtSomeone;
    /**
     * 接口是否通知已告警
     */
    private Boolean alerted;

}