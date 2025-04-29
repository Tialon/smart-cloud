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
package io.github.smart.cloud.code.generate.config;

import io.github.smart.cloud.mask.MaskLog;
import io.github.smart.cloud.mask.MaskRule;

import java.nio.charset.StandardCharsets;

/**
 * 配置常量
 *
 * @author collin
 * @date 2019-07-15
 */
public class Config {

    /**
     * 默认编码（utf-8）
     */
    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    /**
     * 代码生成日期格式
     */
    public static final String CODE_CREATE_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 配置文件所在路径
     */
    public static final String CONFIG_PATH = "config/";
    /**
     * 总配置文件名
     */
    public static final String CONFIG_NAME = "generate_config";
    /**
     * 配置文key
     */
    public static final String PROPERTIES_KEY = "config.yaml";
    /**
     * 模板所在位置
     */
    public static final String TEMPLATE_PATH = "/template";
    /**
     * maven工程源码目录
     */
    public static final String SRC_MAIN_JAVA = "src/main/java/";
    /**
     * java文件名后缀
     */
    public static final String JAVA_FILE_SUFFIX = ".java";
    /**
     * @date注释标记
     */
    public static final String DATE_ANNOTATION_TAG = "@date";
    /**
     * 多个表用隔开的分隔符
     */
    public static final String TABLES_SEPARATOR = ",";

    /**
     * mask相关包名
     */
    public static class MaskPackage {
        /**
         * MaskRule包名
         */
        public static final String MASK_RULE = MaskRule.class.getTypeName();
        /**
         * MaskLog包名
         */
        public static final String MASK_LOG = MaskLog.class.getTypeName();

        private MaskPackage() {
        }
    }

}