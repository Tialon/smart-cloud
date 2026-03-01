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
package io.github.smart.cloud.code.generate.core.impl;

import io.github.smart.cloud.code.generate.bo.ColumnMetaDataBO;
import io.github.smart.cloud.code.generate.bo.TableMetaDataBO;
import io.github.smart.cloud.code.generate.bo.template.buildparam.TemplateBuildParamContext;
import io.github.smart.cloud.code.generate.bo.template.param.EntityBO;
import io.github.smart.cloud.code.generate.bo.template.param.FieldAttributeBO;
import io.github.smart.cloud.code.generate.config.ClassConstants;
import io.github.smart.cloud.code.generate.core.CodeGenerateUtil;
import io.github.smart.cloud.code.generate.enums.DefaultColumnEnum;
import io.github.smart.cloud.code.generate.enums.FileType;
import io.github.smart.cloud.code.generate.util.JavaTypeUtil;
import io.github.smart.cloud.code.generate.util.TableUtil;
import io.github.smart.cloud.code.generate.util.TemplateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entity源代码文件生成策略实现类
 *
 * @author collin
 * @date 2025-04-29
 * @see FileType#ENTITY
 */
public class EntitySourceFileGenerateStrategy extends AbstractSourceFileGenerateStrategy<EntityBO> {

    @Override
    public FileType getFileType() {
        return FileType.ENTITY;
    }

    @Override
    protected String getBasePath(TemplateBuildParamContext context) {
        return context.getCode().getProject().getPath().getService();
    }

    @Override
    protected EntityBO buildTemplateParams(TemplateBuildParamContext context) {
        TableMetaDataBO tableMetaData = context.getTableMetaData();
        Set<String> encryptFields = TableUtil.getEncryptFields(tableMetaData.getName(), context.getCode());


        EntityBO entityBO = new EntityBO();
        entityBO.setClassComment(context.getClassComment());
        entityBO.setTableName(TableUtil.getTableName(tableMetaData.getName()));
        entityBO.setTableComment(tableMetaData.getComment());
        entityBO.setPackageName(TemplateUtil.buildEntityPackageName(context.getCode().getMainClassPackage()));
        entityBO.setClassName(TemplateUtil.buildEntityClassName(tableMetaData.getName()));

        List<FieldAttributeBO> attributes = new ArrayList<>();
        entityBO.setAttributes(attributes);
        entityBO.setImportPackages(CodeGenerateUtil.buildEntityImportPackages(context));
        for (ColumnMetaDataBO columnMetaData : context.getColumnMetaDatas()) {
            if (DefaultColumnEnum.contains(columnMetaData.getName())) {
                continue;
            }
            FieldAttributeBO entityAttribute = new FieldAttributeBO();
            entityAttribute.setName(TableUtil.getAttibuteName(columnMetaData.getName()));
            entityAttribute.setColumnName(columnMetaData.getName());
            entityAttribute.setComment(columnMetaData.getComment());

            // 主键
            entityAttribute.setPrimaryKey(columnMetaData.getPrimaryKey());

            // 加密字段
            if (encryptFields.contains(columnMetaData.getName())) {
                entityAttribute.setJavaType(ClassConstants.CRYPT_FIELD_CLASS_NAME);
            } else {
                entityAttribute.setJavaType(JavaTypeUtil.getByJdbcType(columnMetaData.getJdbcType(), columnMetaData.getLength()));
            }

            attributes.add(entityAttribute);
        }
        return entityBO;
    }

}