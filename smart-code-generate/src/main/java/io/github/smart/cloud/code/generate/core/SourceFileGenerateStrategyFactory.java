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
package io.github.smart.cloud.code.generate.core;

import com.google.common.base.Preconditions;
import com.google.common.reflect.ClassPath;
import io.github.smart.cloud.code.generate.bo.template.buildparam.TemplateBuildParamContext;
import io.github.smart.cloud.code.generate.core.impl.AbstractSourceFileGenerateStrategy;
import io.github.smart.cloud.code.generate.enums.FileType;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 源代码文件生成策略工厂
 *
 * @author collin
 * @date 2025-04-29
 */
public class SourceFileGenerateStrategyFactory {

    private static final Map<FileType, ISourceFileGenerateStrategy> SOURCE_FILE_GENERATE_STRATEGY_MAP = new HashMap<>();

    static {
        String packageName = AbstractSourceFileGenerateStrategy.class.getPackage().getName();

        try {
            Set<Class<? extends ISourceFileGenerateStrategy>> sourceFileGenerateStrategyClasses = findImplementations(packageName, ISourceFileGenerateStrategy.class);
            if (!CollectionUtils.isEmpty(sourceFileGenerateStrategyClasses)) {
                for (Class<? extends ISourceFileGenerateStrategy> sourceFileGenerateStrategyClass : sourceFileGenerateStrategyClasses) {
                    ISourceFileGenerateStrategy sourceFileGenerateStrategy = sourceFileGenerateStrategyClass.newInstance();
                    SOURCE_FILE_GENERATE_STRATEGY_MAP.put(sourceFileGenerateStrategy.getFileType(), sourceFileGenerateStrategy);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成源代码文件
     *
     * @param fileType
     * @param context
     * @throws Exception
     */
    public static void generateSourceFile(FileType fileType, TemplateBuildParamContext context) throws Exception {
        ISourceFileGenerateStrategy sourceFileGenerateStrategy = SOURCE_FILE_GENERATE_STRATEGY_MAP.get(fileType);
        Preconditions.checkNotNull(sourceFileGenerateStrategy, String.format("not find source file generate strategy of %s", fileType.name()));

        sourceFileGenerateStrategy.generateSourceFile(context);
    }

    /**
     * 查找指定包下实现某接口的所有非抽象类
     *
     * @param packageName     包名 (如：com.example)
     * @param targetInterface 目标接口
     * @return 实现类的Class集合
     */
    private static <T> Set<Class<? extends T>> findImplementations(
            String packageName,
            Class<T> targetInterface) throws IOException {
        return ClassPath.from(Thread.currentThread().getContextClassLoader())
                .getTopLevelClassesRecursive(packageName).stream()
                .map(ClassPath.ClassInfo::load)
                // 排除接口自身
                .filter(clazz -> !clazz.isInterface())
                // 排除抽象类
                .filter(clazz -> !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()))
                // 类型检查
                .filter(targetInterface::isAssignableFrom)
                .map(clazz -> (Class<? extends T>) clazz)
                .collect(Collectors.toSet());
    }

}