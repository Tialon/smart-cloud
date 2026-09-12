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
package io.github.smart.cloud.code.generate.bo.template.buildparam;

import io.github.smart.cloud.code.generate.bo.ColumnMetaDataBO;
import io.github.smart.cloud.code.generate.bo.TableMetaDataBO;
import io.github.smart.cloud.code.generate.bo.template.param.ClassCommentBO;
import io.github.smart.cloud.code.generate.properties.CodeProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 生成源代码文件策略类请求参数
 *
 * @author collin
 * @date 2025-04-29
 */
@Getter
@Setter
@ToString
public class TemplateBuildParamContext {

    /**
     * 数据库表元数据信息
     */
    private TableMetaDataBO tableMetaData;
    /**
     * 表字段元数据信息
     */
    private List<ColumnMetaDataBO> columnMetaDatas;
    /**
     * 类注释信息
     */
    private ClassCommentBO classComment;

    /**
     * 配置属性
     */
    private CodeProperties code;

}