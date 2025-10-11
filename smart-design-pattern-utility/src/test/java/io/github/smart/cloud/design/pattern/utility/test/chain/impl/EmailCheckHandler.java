package io.github.smart.cloud.design.pattern.utility.test.chain.impl;

import io.github.smart.cloud.design.pattern.utility.chain.HandlerResult;
import io.github.smart.cloud.design.pattern.utility.chain.OrderedHandler;

/**
 * @author collin.li
 * @date 2025-10-11
 */
public class EmailCheckHandler implements OrderedHandler<ChainContext> {

    @Override
    public HandlerResult handle(ChainContext context) {
        User u = context.getUser();
        if (!u.getEmail().contains("@")) {
            return HandlerResult.fail("邮箱格式不正确");
        }
        return HandlerResult.success();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}