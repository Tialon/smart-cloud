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
package io.github.smart.cloud.design.pattern.utility.test.chain;

import io.github.smart.cloud.design.pattern.utility.chain.Handler;
import io.github.smart.cloud.design.pattern.utility.chain.HandlerChainFactory;
import io.github.smart.cloud.design.pattern.utility.chain.HandlerResult;
import io.github.smart.cloud.design.pattern.utility.chain.OrderedHandler;
import io.github.smart.cloud.design.pattern.utility.test.chain.impl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 责任链设计模式测试用例
 *
 * @author collin.li
 * @date 2025-10-11
 */
public class ResponsibilityChainTest {

    @Test
    void test() {
        User user = new User();
        user.setUsername("test");
        user.setEmail("invalid-email");
        user.setPassword("weak");

        List<OrderedHandler<ChainContext>> handlers = new ArrayList<>();
        handlers.add(new UsernameCheckHandler());
        handlers.add(new EmailCheckHandler());
        handlers.add(new PasswordCheckHandler());

        HandlerResult handlerResult = HandlerChainFactory.sortAndCreate(handlers)
                .execute(new ChainContext(user));
        System.out.println(handlerResult);

        Assertions.assertNotNull(handlerResult);
        Assertions.assertFalse(handlerResult.isPass());
    }

}