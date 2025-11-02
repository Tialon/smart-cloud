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
 * 接口请求汇总信息
 *
 * @author collin
 * @date 2025-09-21
 */
@Getter
@Setter
@ToString
public class ApiRequestSummaryDTO {

    /**
     * 请求总数
     */
    private long totalCount = 0L;

    // ----start:异常接口信息
    /**
     * 失败数
     */
    private long failCount = 0L;
    /**
     * 异常
     */
    private Throwable throwable;
    /**
     * 异常链路号
     */
    private String errorTraceId;
    /**
     * 异常接口通知已告警
     */
    private boolean errorAlerted;
    // ----end:异常接口信息

    // ----start:慢接口信息
    /**
     * 慢接口数
     */
    private long slowCount = 0L;
    /**
     * 最大耗时（毫秒）
     */
    private long maxCost = 0L;
    /**
     * 慢接口链路号
     */
    private String slowTraceId;
    /**
     * 慢接口通知已告警
     */
    private boolean slowAlerted;
    // ----end:慢接口信息

}