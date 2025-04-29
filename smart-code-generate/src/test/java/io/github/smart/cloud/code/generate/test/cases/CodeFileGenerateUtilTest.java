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
package io.github.smart.cloud.code.generate.test.cases;

import io.github.smart.cloud.code.generate.core.CodeGenerateUtil;
import io.github.smart.cloud.code.generate.test.util.CompilerUtil;
import io.github.smart.cloud.code.generate.util.PathUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CodeFileGenerateUtilTest {

    @Test
    void testAuth() throws Exception {
        CodeGenerateUtil.init();
        Iterable<String> classNames = Arrays.asList(
                // response
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/auth/response/entity/PermissionInfoEntityRespVO.java",
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/auth/response/entity/RoleInfoEntityRespVO.java",
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/auth/response/entity/RolePermissionRelaEntityRespVO.java",
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/auth/response/entity/UserRoleRelaEntityRespVO.java",
                // entity
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/entity/PermissionInfoEntity.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/entity/RoleInfoEntity.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/entity/RolePermissionRelaEntity.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/entity/UserRoleRelaEntity.java",
                // mapper
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/mapper/PermissionInfoMapper.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/mapper/RoleInfoMapper.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/mapper/RolePermissionRelaMapper.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/mapper/UserRoleRelaMapper.java",
                // repository
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/repository/PermissionInfoRepository.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/repository/RoleInfoRepository.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/repository/RolePermissionRelaRepository.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/auth/repository/UserRoleRelaRepository.java"
        );
        Assertions.assertThat(CompilerUtil.compile(classNames)).isEmpty();
    }

    @Test
    void testUser() throws Exception {
        CodeGenerateUtil.init("config/basic_user.yaml");
        Iterable<String> classNames = Arrays.asList(
                // response
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/user/response/entity/LoginInfoEntityRespVO.java",
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/basic/rpc/user/response/entity/UserInfoEntityRespVO.java",
                // entity
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/entity/LoginInfoEntity.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/entity/UserInfoEntity.java",
                // mapper
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/mapper/LoginInfoMapper.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/mapper/UserInfoMapper.java",
                // repository
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/repository/LoginInfoRepository.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/basic/user/repository/UserInfoRepository.java"
        );
        Assertions.assertThat(CompilerUtil.compile(classNames)).isEmpty();
    }

    @Test
    void testOrder() throws Exception {
        CodeGenerateUtil.init("config/mall_order.yaml");
        Iterable<String> classNames = Arrays.asList(
                // response
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/mall/rpc/order/response/entity/OrderBillEntityRespVO.java",
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/mall/rpc/order/response/entity/OrderDeliveryInfoEntityRespVO.java",
                // entity
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/entity/OrderBillEntity.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/entity/OrderDeliveryInfoEntity.java",
                // mapper
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/mapper/OrderBillMapper.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/mapper/OrderDeliveryInfoMapper.java",
                // repository
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/repository/OrderBillRepository.java",
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/order/repository/OrderDeliveryInfoRepository.java"
        );
        Assertions.assertThat(CompilerUtil.compile(classNames)).isEmpty();
    }

    @Test
    void testProduct() throws Exception {
        CodeGenerateUtil.init("config/mall_product.yaml");
        Iterable<String> classNames = Arrays.asList(
                // response
                PathUtil.getDefaultRpcDir() + "src/main/java/org/smartframework/cloud/examples/mall/rpc/product/response/entity/ProductInfoEntityRespVO.java",
                // entity
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/product/entity/ProductInfoEntity.java",
                // mapper
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/product/mapper/ProductInfoMapper.java",
                // repository
                PathUtil.getDefaultServiceDir() + "src/main/java/org/smartframework/cloud/examples/mall/product/repository/ProductInfoRepository.java"
        );
        Assertions.assertThat(CompilerUtil.compile(classNames)).isEmpty();
    }

}