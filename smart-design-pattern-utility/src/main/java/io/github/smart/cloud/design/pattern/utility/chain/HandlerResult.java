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
package io.github.smart.cloud.design.pattern.utility.chain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 责任链处理结果
 *
 * @author collin.li
 * @date 2025-10-11
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class HandlerResult {

    /**
     * 是否通过
     */
    private boolean pass;
    /**
     * 未通过时的消息
     */
    private String message;

    /**
     * 失败
     *
     * @param message
     * @return
     */
    public static HandlerResult fail(String message) {
        return new HandlerResult(false, message);
    }

    /**
     * 成功
     *
     * @return
     */
    public static HandlerResult success() {
        return new HandlerResult(true, null);
    }

}