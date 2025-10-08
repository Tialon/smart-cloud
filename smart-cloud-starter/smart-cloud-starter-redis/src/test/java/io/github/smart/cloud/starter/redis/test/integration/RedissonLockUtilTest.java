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
package io.github.smart.cloud.starter.redis.test.integration;

import io.github.smart.cloud.starter.redis.util.RedissonLockUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author collin
 * @date 2025-10-08
 */
class RedissonLockUtilTest extends AbstractRedisIntegrationTest {

    @Autowired
    private RedissonLockUtil redissonLockUtil;

    @Test
    void testTryLockAndExecuteWithConsumer() {
        boolean success = redissonLockUtil.tryLockAndExecute("test1", unused -> {

        });
        Assertions.assertThat(success).isTrue();
    }

    @Test
    void testTryLockAndExecuteWithFunction() {
        boolean success = redissonLockUtil.tryLockAndExecute("", unused -> true);
        Assertions.assertThat(success).isTrue();
    }

}