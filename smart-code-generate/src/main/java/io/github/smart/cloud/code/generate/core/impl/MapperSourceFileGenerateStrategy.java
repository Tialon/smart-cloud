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

import io.github.smart.cloud.code.generate.bo.TableMetaDataBO;
import io.github.smart.cloud.code.generate.bo.template.buildparam.TemplateBuildParamContext;
import io.github.smart.cloud.code.generate.bo.template.param.BaseMapperBO;
import io.github.smart.cloud.code.generate.enums.FileType;
import io.github.smart.cloud.code.generate.util.TemplateUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * mapper源代码文件生成策略实现类
 *
 * @author collin
 * @date 2025-04-29
 * @see FileType#MAPPER
 */
public class MapperSourceFileGenerateStrategy extends AbstractSourceFileGenerateStrategy<BaseMapperBO> {

    @Override
    public FileType getFileType() {
        return FileType.MAPPER;
    }

    @Override
    protected String getBasePath(TemplateBuildParamContext context) {
        return context.getCode().getProject().getPath().getService();
    }

    @Override
    protected BaseMapperBO buildTemplateParams(TemplateBuildParamContext context) {
        TableMetaDataBO tableMetaData = context.getTableMetaData();
        String mainClassPackage = context.getCode().getMainClassPackage();

        BaseMapperBO baseMapperBO = new BaseMapperBO();
        baseMapperBO.setClassComment(context.getClassComment());
        baseMapperBO.setTableComment(tableMetaData.getComment());
        baseMapperBO.setPackageName(mainClassPackage + getFileType().getPackageSuffix());
        baseMapperBO.setClassName(TemplateUtil.buildMapperClassName(tableMetaData.getName()));

        String entityClassName = TemplateUtil.buildEntityClassName(tableMetaData.getName());
        Set<String> importPackages = new HashSet<>(1);
        // entity package
        importPackages.add(TemplateUtil.buildImportEntityClassPackage(mainClassPackage, entityClassName));
        baseMapperBO.setImportPackages(importPackages);

        baseMapperBO.setEntityClassName(entityClassName);
        return baseMapperBO;
    }

}