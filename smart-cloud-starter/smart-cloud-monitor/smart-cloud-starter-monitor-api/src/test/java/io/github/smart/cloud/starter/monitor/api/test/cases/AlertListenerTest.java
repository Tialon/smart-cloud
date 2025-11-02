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
package io.github.smart.cloud.starter.monitor.api.test.cases;

import io.github.smart.cloud.starter.monitor.api.dto.ApiExceptionAlertDTO;
import io.github.smart.cloud.starter.monitor.api.event.ApiMonitorAlertEvent;
import io.github.smart.cloud.starter.monitor.api.listener.alert.ApiExceptionMonitorWeworkAlertListener;
import io.github.smart.cloud.starter.monitor.api.test.prepare.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AlertListenerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    // 用@SpyBean包装监听器，保留原功能并允许验证
    @SpyBean
    private ApiExceptionMonitorWeworkAlertListener apiExceptionMonitorWeworkAlertListener;

    @Test
    void testEvent() {
        ApiExceptionAlertDTO apiExceptionAlertDTO = new ApiExceptionAlertDTO();
        apiExceptionAlertDTO.setName("test");
        apiExceptionAlertDTO.setThrowable(new NullPointerException());

        ApiMonitorAlertEvent<ApiExceptionAlertDTO> event = ApiMonitorAlertEvent.buildImmediateEvent(this, apiExceptionAlertDTO);
        applicationEventPublisher.publishEvent(event);
        // 验证监听器的onApplicationEvent方法被调用（参数为发布的事件）
        Mockito.verify(apiExceptionMonitorWeworkAlertListener, Mockito.times(1))
                .onApplicationEvent(Mockito.eq(event));
    }

}