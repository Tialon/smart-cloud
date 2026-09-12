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
package io.github.smart.cloud.starter.redis.util;

import io.github.smart.cloud.exception.AcquiredLockFailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * redisson锁工具类
 *
 * @author collin
 * @date 2025-10-08
 */
@Slf4j
@RequiredArgsConstructor
public class RedissonLockUtil {

    private final RedissonClient redissonClient;

    /**
     * 尝试获取锁并执行业务逻辑
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @return
     */
    public boolean tryLockAndExecute(String lockKey, Consumer<Void> successAction) {
        return tryLockAndExecute(lockKey, successAction, null, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行业务逻辑，获取锁失败时抛异常
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @return
     * @throws AcquiredLockFailException 获取锁失败时抛异常
     */
    public boolean tryLockAndExecuteOrThrow(String lockKey, Consumer<Void> successAction) {
        return tryLockAndExecute(lockKey, successAction, unused -> {
            log.warn("It was failed when acquiring the lock[{}]", lockKey);
            throw new AcquiredLockFailException();
        }, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行无返回值的操作
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @param failAction    获取锁失败时要执行的操作
     */
    public boolean tryLockAndExecute(String lockKey, Consumer<Void> successAction, Consumer<Void> failAction) {
        return tryLockAndExecute(lockKey, successAction, failAction, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行无返回值的操作
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @param failAction    获取锁失败时要执行的操作
     * @param waitTime      获取锁的最大等待时间
     * @param leaseTime     锁的持有时间
     * @param timeUnit      时间单位
     * @return 是否成功获取锁并执行操作
     */
    public boolean tryLockAndExecute(String lockKey, Consumer<Void> successAction, Consumer<Void> failAction,
                                     long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (locked) {
                successAction.accept(null);
                return true;
            } else {
                if (failAction != null) {
                    failAction.accept(null);
                }
            }
        } catch (InterruptedException e) {
            log.warn("It was interrupted when acquiring the lock[{}]", lockKey);
            throw new AcquiredLockFailException();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return false;
    }

    /**
     * 尝试获取锁并执行有返回值的操作
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @param <T>           返回值类型
     * @return 操作结果
     */
    public <T> T tryLockAndExecute(String lockKey, Function<Void, T> successAction) {
        return tryLockAndExecute(lockKey, successAction, null, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行业务逻辑，获取锁失败时抛异常
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @return
     * @throws AcquiredLockFailException 获取锁失败时抛异常
     */
    public <T> T tryLockAndExecuteOrThrow(String lockKey, Function<Void, T> successAction) {
        return tryLockAndExecute(lockKey, successAction, unused -> {
            log.warn("It was failed when acquiring the lock[{}]", lockKey);
            throw new AcquiredLockFailException();
        }, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行有返回值的操作
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @param failAction    获取锁失败时要执行的操作
     * @param <T>           返回值类型
     * @return 操作结果
     */
    public <T> T tryLockAndExecute(String lockKey, Function<Void, T> successAction, Function<Void, T> failAction) {
        return tryLockAndExecute(lockKey, successAction, failAction, 0, -1, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取锁并执行有返回值的操作
     *
     * @param lockKey       锁的key
     * @param successAction 成功获取锁要执行的操作
     * @param failAction    获取锁失败时要执行的操作
     * @param waitTime      获取锁的最大等待时间
     * @param leaseTime     锁的持有时间
     * @param timeUnit      时间单位
     * @param <T>           返回值类型
     * @return 操作结果
     */
    public <T> T tryLockAndExecute(String lockKey, Function<Void, T> successAction, Function<Void, T> failAction,
                                   long waitTime, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTime, leaseTime, timeUnit);
            if (locked) {
                return successAction.apply(null);
            }

            if (failAction == null) {
                log.warn("It was failed when acquiring the lock[{}]", lockKey);
                throw new AcquiredLockFailException();
            }
            return failAction.apply(null);
        } catch (InterruptedException e) {
            log.warn("It was interrupted when acquiring the lock[{}]", lockKey, e);
            throw new AcquiredLockFailException();
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取锁并执行操作（阻塞直到获取锁）
     *
     * @param lockKey 锁的key
     * @param action  要执行的操作
     */
    public void lockAndExecute(String lockKey, Consumer<Void> action) {
        lockAndExecute(lockKey, action, -1, TimeUnit.SECONDS);
    }

    /**
     * 获取锁并执行操作（阻塞直到获取锁）
     *
     * @param lockKey   锁的key
     * @param action    要执行的操作
     * @param leaseTime 锁的持有时间
     * @param timeUnit  时间单位
     */
    public void lockAndExecute(String lockKey, Consumer<Void> action,
                               long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (leaseTime >= 0) {
                lock.lock(leaseTime, timeUnit);
            } else {
                lock.lock();
            }
            action.accept(null);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取锁并执行有返回值的操作（阻塞直到获取锁）
     *
     * @param lockKey 锁的key
     * @param action  要执行的操作
     * @param <T>     返回值类型
     * @return 操作结果
     */
    public <T> T lockAndExecute(String lockKey, Function<Void, T> action) {
        return lockAndExecute(lockKey, action, -1, TimeUnit.SECONDS);
    }

    /**
     * 获取锁并执行有返回值的操作（阻塞直到获取锁）
     *
     * @param lockKey   锁的key
     * @param action    要执行的操作
     * @param leaseTime 锁的持有时间
     * @param timeUnit  时间单位
     * @param <T>       返回值类型
     * @return 操作结果
     */
    public <T> T lockAndExecute(String lockKey, Function<Void, T> action, long leaseTime, TimeUnit timeUnit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            if (leaseTime >= 0) {
                lock.lock(leaseTime, timeUnit);
            } else {
                lock.lock();
            }
            return action.apply(null);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}