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
package io.github.smart.cloud.design.pattern.utility.test.chain.impl;

import io.github.smart.cloud.design.pattern.utility.chain.Handler;
import io.github.smart.cloud.design.pattern.utility.chain.HandlerResult;
import io.github.smart.cloud.design.pattern.utility.chain.OrderedHandler;

/**
 * @author collin.li
 * @date 2025-10-11
 */
public class PasswordCheckHandler implements OrderedHandler<ChainContext> {

    @Override
    public HandlerResult handle(ChainContext context) {
        User u = context.getUser();
        if (u.getPassword() == null || u.getPassword().length() < 8) {
            return HandlerResult.fail("密码必须至少8个字符");
        }
        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 3;
    }
}