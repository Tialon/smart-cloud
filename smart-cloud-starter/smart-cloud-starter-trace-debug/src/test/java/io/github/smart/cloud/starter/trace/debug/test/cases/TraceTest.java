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
package io.github.smart.cloud.starter.trace.debug.test.cases;

import io.github.smart.cloud.starter.trace.debug.constants.TraceDebugConstants;
import io.github.smart.cloud.starter.trace.debug.enums.EnumTraceDebugType;
import io.github.smart.cloud.starter.trace.debug.test.prepare.openfeign.IOrderFeign;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TraceTest extends AbstractTest {

    @Autowired
    private IOrderFeign orderFeign;

    /**
     * 只打印耗时——web接口测试
     *
     * @throws Exception
     */
    @Test
    public void testCost(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/order/query")
                        .header(TraceDebugConstants.TRACE_DEBUG_TYPE, EnumTraceDebugType.ONLY_COST.getValue())
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("success1"));

        Assertions.assertThat(output.getOut()).contains("trace==>OrderController#query costType");
    }

    /**
     * 只打印出入参——web接口测试
     *
     * @throws Exception
     */
    @Test
    public void testOnlyInputOutput(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/order/query")
                        .header(TraceDebugConstants.TRACE_DEBUG_TYPE, EnumTraceDebugType.ONLY_INPUT_OUTPUT.getValue())
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("success1"));
        Assertions.assertThat(output.getOut()).contains("trace==>OrderController#query args=[1], result={");
    }

    /**
     * 打印耗时汇总
     *
     * @throws Exception
     */
    @Test
    public void testSummary(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/order/query")
                        .header(TraceDebugConstants.TRACE_DEBUG_TYPE, EnumTraceDebugType.COST_SUMMARY.getValue())
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("success1"));
        Assertions.assertThat(output.getOut())
                .contains("trace==>OrderController#query costType")
                .contains("costSummarys=[{");
    }

    /**
     * 打印耗时和出入参——feign接口测试
     *
     * @throws Exception
     */
    @Test
    public void testAll(CapturedOutput output) throws Exception {
        ResponseEntity<String> responseEntity = orderFeign.query(1, EnumTraceDebugType.ALL.getValue());
        Assertions.assertThat(responseEntity).isNotNull();
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(output.getOut())
                .contains("trace==>OrderController#query cost=")
                .contains("args=")
                .contains("result={");
    }

}