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