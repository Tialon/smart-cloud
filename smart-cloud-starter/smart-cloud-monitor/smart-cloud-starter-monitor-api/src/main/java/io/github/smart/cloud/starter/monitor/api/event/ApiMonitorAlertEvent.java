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

import io.github.smart.cloud.starter.monitor.api.dto.ApiAlertCommonDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 接口异常告警事件
 *
 * @author collin
 * @date 2024-07-01
 */
@Getter
public class ApiMonitorAlertEvent<T extends ApiAlertCommonDTO> extends ApplicationEvent implements ResolvableTypeProvider {

    private static final long serialVersionUID = 1L;

    private final List<T> alertInfos;
    /**
     * 是否是立即提醒告警
     */
    private final boolean immediateAlert;

    private ApiMonitorAlertEvent(Object source, boolean immediateAlert, List<T> alertInfos) {
        super(source);
        this.alertInfos = alertInfos;
        this.immediateAlert = immediateAlert;
    }

    @Override
    public ResolvableType getResolvableType() {
        if (CollectionUtils.isEmpty(alertInfos)) {
            return null;
        }

        // 解析当前类（GenericListEvent）的泛型参数T
        return ResolvableType.forClassWithGenerics(ApiMonitorAlertEvent.class, alertInfos.get(0).getClass());
    }

    public static <P extends ApiAlertCommonDTO> ApiMonitorAlertEvent<P> buildImmediateEvent(Object source, P alertInfo) {
        return new ApiMonitorAlertEvent(source, true, Arrays.asList(alertInfo));
    }


    public static <P extends ApiAlertCommonDTO> ApiMonitorAlertEvent<P> buildSummaryEvents(Object source, List<P> alertInfos) {
        return new ApiMonitorAlertEvent(source, false, alertInfos);
    }

}