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

import java.math.BigDecimal;
import java.util.concurrent.atomic.LongAdder;

/**
 * 慢接口信息
 *
 * @author collin
 * @date 2024-01-6
 */
@Getter
@Setter
@ToString
public class ApiSlowAlertDTO extends ApiAlertCommonDTO {

    /**
     * 最大耗时（单位毫秒）
     */
    private Long maxCost;
    /**
     * 慢接口数
     */
    private Long slowCount;
    /**
     * 慢接口百分比
     */
    private BigDecimal slowRate;
    /**
     * 是否@提醒
     */
    private boolean needAtSomeone;

}
