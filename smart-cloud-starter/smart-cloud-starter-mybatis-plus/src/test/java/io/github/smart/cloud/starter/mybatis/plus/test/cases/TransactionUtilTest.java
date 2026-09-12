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
package io.github.smart.cloud.starter.mybatis.plus.test.cases;

import io.github.smart.cloud.starter.mybatis.plus.enums.DeleteState;
import io.github.smart.cloud.starter.mybatis.plus.test.prepare.mybatisplus.MybatisplusApp;
import io.github.smart.cloud.starter.mybatis.plus.test.prepare.mybatisplus.entity.ProductInfoEntity;
import io.github.smart.cloud.starter.mybatis.plus.test.prepare.mybatisplus.repository.ProductInfoRepository;
import io.github.smart.cloud.starter.mybatis.plus.util.TransactionUtil;
import io.github.smart.cloud.utility.NonceUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Date;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MybatisplusApp.class, args = "--spring.profiles.active=mybatisplus")
public class TransactionUtilTest {

    @Autowired
    private ProductInfoRepository productInfoRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void testExecuteInTransactionForConsumer() {
        TransactionUtil transactionUtil = new TransactionUtil(transactionManager);
        // 正常
        {
            ProductInfoEntity prepareProductEntity = buildProductInfoEntity("test");
            transactionUtil.executeInTransaction(transactionStatus -> {
                productInfoRepository.save(prepareProductEntity);
            });
            ProductInfoEntity dbEntity = productInfoRepository.getById(prepareProductEntity.getId());
            Assertions.assertThat(dbEntity).isNotNull();
        }

        // 异常回滚
        {
            ProductInfoEntity prepareProductEntity = buildProductInfoEntity("test");
            try {
                transactionUtil.executeInTransaction(transactionStatus -> {
                    productInfoRepository.save(prepareProductEntity);
                    int i = 1 / 0;
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            ProductInfoEntity dbEntity = productInfoRepository.getById(prepareProductEntity.getId());
            Assertions.assertThat(dbEntity).isNull();
        }
    }

    @Test
    void testExecuteInTransactionForFunction() {
        TransactionUtil transactionUtil = new TransactionUtil(transactionManager);
        // 正常
        {
            ProductInfoEntity prepareProductEntity = buildProductInfoEntity("test");
            boolean success = transactionUtil.executeInTransaction(transactionStatus -> {
                productInfoRepository.save(prepareProductEntity);
                return true;
            });
            ProductInfoEntity dbEntity = productInfoRepository.getById(prepareProductEntity.getId());
            Assertions.assertThat(success).isTrue();
            Assertions.assertThat(dbEntity).isNotNull();
        }

        // 异常回滚
        {
            ProductInfoEntity prepareProductEntity = buildProductInfoEntity("test");
            try {
                transactionUtil.executeInTransaction(transactionStatus -> {
                    productInfoRepository.save(prepareProductEntity);
                    int i = 1 / 0;
                    return false;
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            ProductInfoEntity dbEntity = productInfoRepository.getById(prepareProductEntity.getId());
            Assertions.assertThat(dbEntity).isNull();
        }
    }

    private ProductInfoEntity buildProductInfoEntity(String name) {
        ProductInfoEntity entity = new ProductInfoEntity();
        entity.setId(NonceUtil.nextId());
        entity.setInsertTime(new Date());
        entity.setDelState(DeleteState.NORMAL);
        entity.setName(name);
        entity.setSellPrice(100L);
        entity.setStock(10L);
        entity.setInsertUser(10L);
        return entity;
    }

}