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
package io.github.smart.cloud.starter.trace.debug.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 当前线程方法耗时信息统计上下文
 *
 * @author collin.li
 * @date 2026-01-09
 */
@Slf4j
public class TraceCostContext {

    /**
     * 记录当前线程方法耗时信息，用于汇总统计
     */
    public static final TransmittableThreadLocal<Map<String, List<Long>>> CONTEXT = new TransmittableThreadLocal<>();

    public static void init() {
        CONTEXT.set(new HashMap<>(128));
    }

    /**
     * 打印耗时汇总信息
     */
    public static void printCostSummary() {
        Map<String, List<Long>> traceCostMap = CONTEXT.get();
        if (traceCostMap == null) {
            return;
        }

        // 直接定义double类型，方便后面小数处理
        double ratio = TimeUnit.MILLISECONDS.toNanos(1);
        // 统计汇总
        List<CostSummaryDTO> costSummarys = new ArrayList<>(traceCostMap.size());
        for (Map.Entry<String, List<Long>> entry : traceCostMap.entrySet()) {
            List<Long> costs = entry.getValue();
            CostSummaryDTO dto = new CostSummaryDTO();
            dto.setName(entry.getKey());
            int count = costs.size();
            dto.setCount(count);
            dto.setMinCost(Collections.min(costs) / ratio);
            dto.setMaxCost(Collections.max(costs) / ratio);
            long totalCost = costs.stream().reduce(0L, Long::sum);
            dto.setTotalCost(totalCost / ratio);
            long averageCost = totalCost / count;
            dto.setAverageCost(averageCost / ratio);

            costSummarys.add(dto);
        }

        // 按平均耗时倒序排序
        costSummarys.sort(Comparator.comparingDouble(CostSummaryDTO::getAverageCost).reversed());
        log.info("costSummarys={}", JacksonUtil.toJson(costSummarys));
    }

    /**
     * 添加耗时记录
     *
     * @param methodName
     * @param cost
     */
    public static void add(String methodName, long cost) {
        Map<String, List<Long>> traceCostMap = CONTEXT.get();
        if (traceCostMap != null) {
            List<Long> costs = traceCostMap.get(methodName);
            if (costs == null) {
                costs = new ArrayList<>();
                traceCostMap.put(methodName, costs);
            }
            costs.add(cost);
        }
    }

    public static void remove() {
        CONTEXT.remove();
    }

    @Getter
    @Setter
    @ToString
    static class CostSummaryDTO {
        /**
         * 方法名称
         */
        private String name;
        /**
         * 请求总数
         */
        private Integer count;
        /**
         * 最小耗时（单位：毫秒）
         */
        private Double minCost;
        /**
         * 最大耗时（单位：毫秒）
         */
        private Double maxCost;
        /**
         * 平均耗时（单位：毫秒）
         */
        private Double averageCost;
        /**
         * 总耗时（单位：毫秒）
         */
        private Double totalCost;
    }

}