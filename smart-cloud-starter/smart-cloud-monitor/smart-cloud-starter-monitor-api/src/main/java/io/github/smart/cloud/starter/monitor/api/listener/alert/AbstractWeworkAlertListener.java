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
package io.github.smart.cloud.starter.monitor.api.listener.alert;

import io.github.smart.cloud.utility.NetworkUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 企业微信告警监听器抽象类
 *
 * @author collin.li
 * @date 2024-06-16
 */
public abstract class AbstractWeworkAlertListener implements EnvironmentAware, InitializingBean {

    private Environment environment;
    /**
     * 本机IP地址
     */
    protected String ip;
    /**
     * 应用名
     */
    protected String appName;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.ip = NetworkUtil.getLocalIpAddress();
        this.appName = environment.getProperty("spring.application.name");
    }

}