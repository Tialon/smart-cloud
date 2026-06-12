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
import io.github.smart.cloud.code.generate.bo.template.param.RepositoryBO;
import io.github.smart.cloud.code.generate.enums.FileType;
import io.github.smart.cloud.code.generate.util.TemplateUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Repository源代码文件生成策略实现类
 *
 * @author collin
 * @date 2025-04-29
 * @see FileType#REPOSITORY
 */
public class RepositorySourceFileGenerateStrategy extends AbstractSourceFileGenerateStrategy<RepositoryBO> {

    @Override
    public FileType getFileType() {
        return FileType.REPOSITORY;
    }

    @Override
    protected String getBasePath(TemplateBuildParamContext context) {
        return context.getCode().getProject().getPath().getService();
    }

    @Override
    protected RepositoryBO buildTemplateParams(TemplateBuildParamContext context) {
        TableMetaDataBO tableMetaData = context.getTableMetaData();
        String mainClassPackage = context.getCode().getMainClassPackage();

        RepositoryBO repositoryBO = new RepositoryBO();
        repositoryBO.setClassComment(context.getClassComment());
        repositoryBO.setTableComment(tableMetaData.getComment());
        repositoryBO.setPackageName(mainClassPackage + getFileType().getPackageSuffix());
        repositoryBO.setClassName(TemplateUtil.buildRepositoryClassName(tableMetaData.getName()));

        String entityClassName = TemplateUtil.buildEntityClassName(tableMetaData.getName());
        String mapperClassName = TemplateUtil.buildMapperClassName(tableMetaData.getName());
        Set<String> importPackages = new HashSet<>(2);
        // entity package
        String importEntityClassPackage = TemplateUtil.buildImportEntityClassPackage(mainClassPackage, entityClassName);
        importPackages.add(importEntityClassPackage);
        // mapper package
        String importMapperClassPackage = TemplateUtil.buildImportMapperClassPackage(mainClassPackage, mapperClassName);
        importPackages.add(importMapperClassPackage);
        repositoryBO.setImportPackages(importPackages);

        repositoryBO.setEntityClassName(entityClassName);
        repositoryBO.setMapperClassName(mapperClassName);
        return repositoryBO;
    }

}