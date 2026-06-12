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
package io.github.smart.cloud.code.generate.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 待生成的文件类型
 *
 * @author collin
 * @date 2025-04-29
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum FileType {

    /**
     * 实体对象
     */
    ENTITY("Entity.ftl", "Entity", ".entity"),
    /**
     * 实体对象对应的response对象
     */
    ENTITY_RESP_VO("EntityRespVO.ftl", "EntityRespVO", ".response.entity"),
    /**
     * mapper
     */
    MAPPER("Mapper.ftl", "Mapper", ".mapper"),
    /**
     * repository对象（对应mybatis-plus的service实现类）
     */
    REPOSITORY("Repository.ftl", "Repository", ".repository");

    /**
     * 模板文件名
     */
    private String templateFilename;
    /**
     * 类名后缀
     */
    private String classSuffix;
    /**
     * 包名后缀
     */
    private String packageSuffix;

}