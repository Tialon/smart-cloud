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
import io.github.smart.cloud.code.generate.bo.template.param.BaseRespBO;
import io.github.smart.cloud.code.generate.bo.template.param.FieldAttributeBO;
import io.github.smart.cloud.code.generate.core.CodeGenerateUtil;
import io.github.smart.cloud.code.generate.enums.DefaultColumnEnum;
import io.github.smart.cloud.code.generate.enums.FileType;
import io.github.smart.cloud.code.generate.util.JavaTypeUtil;
import io.github.smart.cloud.code.generate.util.TableUtil;
import io.github.smart.cloud.code.generate.util.TemplateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseRespVO源代码文件生成策略实现类
 *
 * @author collin
 * @date 2025-04-29
 * @see FileType#ENTITY_RESP_VO
 */
public class EntityRespVoSourceFileGenerateStrategy extends AbstractSourceFileGenerateStrategy<BaseRespBO> {

    @Override
    public FileType getFileType() {
        return FileType.ENTITY_RESP_VO;
    }

    @Override
    protected String getBasePath(TemplateBuildParamContext context) {
        return context.getCode().getProject().getPath().getRpc();
    }

    @Override
    protected BaseRespBO buildTemplateParams(TemplateBuildParamContext context) {
        TableMetaDataBO tableMetaData = context.getTableMetaData();
        Map<String, Map<String, String>> mask = context.getCode().getMask();
        List<ColumnMetaDataBO> columnMetaDatas = context.getColumnMetaDatas();

        BaseRespBO baseResp = new BaseRespBO();
        baseResp.setClassComment(context.getClassComment());
        baseResp.setTableComment(tableMetaData.getComment());
        baseResp.setPackageName(getBaseRespBodyPackage(context.getCode().getMainClassPackage()));
        baseResp.setClassName(TemplateUtil.buildBaseRespBodyClassName(tableMetaData.getName()));
        baseResp.setImportPackages(CodeGenerateUtil.buildEntityImportPackages(context));

        List<FieldAttributeBO> attributes = new ArrayList<>();
        baseResp.setAttributes(attributes);
        for (ColumnMetaDataBO columnMetaData : columnMetaDatas) {
            if (DefaultColumnEnum.contains(columnMetaData.getName())) {
                continue;
            }
            FieldAttributeBO entityAttribute = new FieldAttributeBO();
            entityAttribute.setName(TableUtil.getAttibuteName(columnMetaData.getName()));
            entityAttribute.setComment(columnMetaData.getComment());
            entityAttribute.setMaskRule(CodeGenerateUtil.getMaskRule(mask, tableMetaData.getName(), columnMetaData.getName()));
            entityAttribute.setJavaType(JavaTypeUtil.getByJdbcType(columnMetaData.getJdbcType(), columnMetaData.getLength()));

            attributes.add(entityAttribute);
        }
        return baseResp;
    }

    /**
     * 获取BaseRespBody包名
     *
     * @param mainClassPackage
     * @return
     */
    private String getBaseRespBodyPackage(String mainClassPackage) {
        int index = mainClassPackage.lastIndexOf('.');

        return mainClassPackage.subSequence(0, index) + ".rpc" + mainClassPackage.substring(index) + getFileType().getPackageSuffix();
    }

}