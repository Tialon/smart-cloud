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
package io.github.smart.cloud.code.generate.util;

import io.github.smart.cloud.code.generate.bo.template.param.ClassCommentBO;
import io.github.smart.cloud.code.generate.config.Config;
import io.github.smart.cloud.code.generate.enums.FileType;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板BO工具类
 *
 * @author collin
 * @date 2019-07-15
 */
public class TemplateUtil {

    private TemplateUtil() {
    }

    /**
     * 获取公共信息（如生成时间、作者等）
     *
     * @param author
     * @return
     */
    public static ClassCommentBO getClassCommentBO(String author) {
        ClassCommentBO classComment = new ClassCommentBO();
        classComment.setCreateDate(new SimpleDateFormat(Config.CODE_CREATE_DATE_FORMAT).format(new Date()));
        classComment.setAuthor(author);
        return classComment;
    }

    /**
     * 构建entity包名
     *
     * @param mainClassPackage
     * @return
     */
    public static String buildEntityPackageName(String mainClassPackage) {
        return mainClassPackage + FileType.ENTITY.getPackageSuffix();
    }

    /**
     * 构建entity类名
     *
     * @param tableName
     * @return
     */
    public static String buildEntityClassName(String tableName) {
        return TableUtil.getEntityClassName(tableName) + FileType.ENTITY.getClassSuffix();
    }

    /**
     * 构建依赖entity类的包名
     *
     * @param mainClassPackage
     * @param entityClassName
     * @return
     */
    public static String buildImportEntityClassPackage(String mainClassPackage, String entityClassName) {
        return mainClassPackage + FileType.ENTITY.getPackageSuffix() + "." + entityClassName;
    }

    /**
     * 构建mapper类名
     *
     * @param tableName
     * @return
     */
    public static String buildMapperClassName(String tableName) {
        return TableUtil.getEntityClassName(tableName) + FileType.MAPPER.getClassSuffix();
    }

    /**
     * 构建依赖mapper类的包名
     *
     * @param mainClassPackage
     * @param mapperClassName
     * @return
     */
    public static String buildImportMapperClassPackage(String mainClassPackage, String mapperClassName) {
        return mainClassPackage + FileType.MAPPER.getPackageSuffix() + "." + mapperClassName;
    }

    /**
     * 构建repository类名
     *
     * @param tableName
     * @return
     */
    public static String buildRepositoryClassName(String tableName) {
        return TableUtil.getEntityClassName(tableName) + FileType.REPOSITORY.getClassSuffix();
    }

    /**
     * 构建RespBody类名
     *
     * @param tableName
     * @return
     */
    public static String buildBaseRespBodyClassName(String tableName) {
        return TableUtil.getEntityClassName(tableName) + FileType.ENTITY_RESP_VO.getClassSuffix();
    }

}