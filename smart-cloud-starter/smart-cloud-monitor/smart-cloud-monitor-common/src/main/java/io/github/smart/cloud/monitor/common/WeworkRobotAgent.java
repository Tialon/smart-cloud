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
package io.github.smart.cloud.monitor.common;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.smart.cloud.exception.ConfigException;
import io.github.smart.cloud.monitor.common.dto.wework.*;
import io.github.smart.cloud.monitor.common.properties.ProxyProperties;
import io.github.smart.cloud.utility.HttpUtil;
import io.github.smart.cloud.utility.JacksonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * 企业微信机器人发送消息
 *
 * @author collin
 * @date 2025-09-21
 */
@Slf4j
@RequiredArgsConstructor
public class WeworkRobotAgent implements InitializingBean {

    private final ProxyProperties proxyProperties;
    private HttpHost proxyHttpHost;
    /**
     * txt格式消息最大长度
     */
    private static final int TXT_CONTENT_MAX_LENGTH = 2048;
    /**
     * markdown格式消息最大长度
     */
    private static final int MARKDOWN_CONTENT_MAX_LENGTH = 4096;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (existProxy(proxyProperties)) {
            this.proxyHttpHost = new HttpHost(proxyProperties.getHost(), proxyProperties.getPort());
        }
    }

    /**
     * 是否存在代理代理
     *
     * @param proxyProperties
     * @return
     */
    private boolean existProxy(ProxyProperties proxyProperties) {
        return (proxyProperties.getHost() != null && proxyProperties.getHost().trim().length() > 0)
                && (proxyProperties.getPort() != null && proxyProperties.getPort() > 0);
    }

    /**
     * 发送企业微信机器人消息
     *
     * @param robotKey
     * @param message
     * @return
     */
    public boolean sendMessage(String robotKey, AbstractWeworkRobotMessageDTO message) {
        // 截掉content超过的部分
        if (message instanceof WeworkRobotMarkdownMessageDTO) {
            WeworkRobotMarkdownMessageDTO markdownMessage = (WeworkRobotMarkdownMessageDTO) message;
            WeworkRobotMessageContentDTO markdownContent = markdownMessage.getMarkdown();
            markdownContent.setContent(truncateContent(markdownContent.getContent(), MARKDOWN_CONTENT_MAX_LENGTH));
        } else if (message instanceof WeworkRobotTextMessageDTO) {
            WeworkRobotTextMessageDTO textMessage = (WeworkRobotTextMessageDTO) message;
            WeworkRobotTextMessageContentDTO textContent = textMessage.getText();
            textContent.setContent(truncateContent(textContent.getContent(), TXT_CONTENT_MAX_LENGTH));
        }

        return sendMessage(robotKey, JacksonUtil.toJson(message));
    }

    /**
     * 截掉超过的部分
     *
     * @param content
     * @param maxLength
     * @return
     */
    private String truncateContent(String content, int maxLength) {
        if (content != null && content.length() > maxLength) {
            log.warn("content before truncate:{}", content);
            return content.substring(0, maxLength);
        }
        return content;
    }

    /**
     * 发送企业微信机器人消息
     *
     * @param robotKey
     * @param msg
     * @return
     */
    private boolean sendMessage(String robotKey, String msg) {
        if (!StringUtils.hasText(robotKey)) {
            throw new ConfigException("The robot key is not configured");
        }

        try {
            String weworkRobotUrl = String.format("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=%s", robotKey);
            String result = HttpUtil.postWithRaw(weworkRobotUrl, msg, proxyHttpHost);
            // {"errcode":0,"errmsg":"ok"}
            if (!StringUtils.hasText(result)) {
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

}