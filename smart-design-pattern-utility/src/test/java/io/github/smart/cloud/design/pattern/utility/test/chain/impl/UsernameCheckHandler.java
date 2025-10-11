package io.github.smart.cloud.design.pattern.utility.test.chain.impl;

import io.github.smart.cloud.design.pattern.utility.chain.Handler;
import io.github.smart.cloud.design.pattern.utility.chain.HandlerResult;
import io.github.smart.cloud.design.pattern.utility.chain.OrderedHandler;

/**
 * @author collin.li
 * @date 2025-10-11
 */
public class UsernameCheckHandler implements OrderedHandler<ChainContext> {

    @Override
    public HandlerResult handle(ChainContext context) {
        User u = context.getUser();
        if (u.getUsername() == null || u.getUsername().length() < 3) {
            return HandlerResult.fail("用户名必须至少3个字符");
        }
        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}