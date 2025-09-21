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
package io.github.smart.cloud.starter.monitor.api.constants;

import io.github.smart.cloud.starter.monitor.api.listener.monitor.ApiMonitorListener;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.ExceptionApiMonitorListener;
import io.github.smart.cloud.starter.monitor.api.listener.monitor.SlowApiMonitorListener;

/**
 * 执行顺序（值越小优先级越高）
 *
 * @author collin.li
 * @date 2025-09-21
 */
public interface OrderConstants {

    /**
     * {@link ApiMonitorListener}执行优先级
     */
    int API_MONITOR_LISTENER = 1;

    /**
     * {@link ExceptionApiMonitorListener}执行优先级
     */
    int EXCEPTION_API_MONITOR_LISTENER = API_MONITOR_LISTENER + 1;

    /**
     * {@link SlowApiMonitorListener}执行优先级
     */
    int SLOW_API_MONITOR_LISTENER = EXCEPTION_API_MONITOR_LISTENER + 1;

}
