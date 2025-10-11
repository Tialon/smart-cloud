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