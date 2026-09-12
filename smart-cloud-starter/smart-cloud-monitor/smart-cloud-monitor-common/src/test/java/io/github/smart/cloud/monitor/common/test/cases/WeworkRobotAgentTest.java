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
package io.github.smart.cloud.monitor.common.test.cases;

import io.github.smart.cloud.monitor.common.WeworkRobotAgent;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotMarkdownMessageDTO;
import io.github.smart.cloud.monitor.common.dto.wework.WeworkRobotTextMessageDTO;
import io.github.smart.cloud.monitor.common.test.prepare.App;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WeworkRobotAgentTest {

    @Autowired
    private WeworkRobotAgent weworkRobotAgent;

    @Test
    void testSendMessageByMarkdown() {
        WeworkRobotMarkdownMessageDTO weworkRobotTextMessage = new WeworkRobotMarkdownMessageDTO("hello markdown");
        boolean success = weworkRobotAgent.sendMessage("cb38ee52-8e80-4101-9aec-78efdfccf8f6", weworkRobotTextMessage);
        Assertions.assertThat(success).isTrue();
    }

    @Test
    void testSendMessageByTxt() {
        WeworkRobotTextMessageDTO weworkRobotTextMessage = new WeworkRobotTextMessageDTO("hello text");
        boolean success = weworkRobotAgent.sendMessage("cb38ee52-8e80-4101-9aec-78efdfccf8f6", weworkRobotTextMessage);
        Assertions.assertThat(success).isTrue();
    }

}