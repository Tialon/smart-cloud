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
package io.github.smart.cloud.starter.monitor.api.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.smart.cloud.exception.ConfigException;
import io.github.smart.cloud.starter.monitor.api.properties.ApiMonitorProperties;
import io.github.smart.cloud.utility.HttpUtil;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;

/**
 * 企业微信机器人
 *
 * @author collin
 * @date 2025-05-17
 */
@Slf4j
@RequiredArgsConstructor
public class WeworkRobotComponent implements InitializingBean {

    private final ApiMonitorProperties apiMonitorProperties;

    private HttpHost proxy;

    /**
     * 发送企业微信机器人消息
     *
     * @param robotKey
     * @param msg
     * @return
     */
    public boolean sendWeworkRobotMessage(String robotKey, String msg) {
        if (StringUtils.isBlank(robotKey)) {
            throw new ConfigException("The robot key is not configured");
        }

        try {
            String weworkRobotUrl = String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", robotKey);
            String result = HttpUtil.postWithRaw(weworkRobotUrl, msg, proxy);
            // {"errcode":0,"errmsg":"ok"}
            if (StringUtils.isBlank(result)) {
                return false;
            }

            JsonNode resultNode = JacksonUtil.parse(result);
            if (resultNode == null) {
                return false;
            }

            JsonNode codeNode = resultNode.get("errcode");
            return codeNode != null && codeNode.asInt() == 0;
        } catch (IOException e) {
            log.error("send http request fail|request={}", msg, e);
            return false;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(apiMonitorProperties.getProxyHost())) {
            proxy = new HttpHost(apiMonitorProperties.getProxyHost(), apiMonitorProperties.getPort());
        }
    }

}