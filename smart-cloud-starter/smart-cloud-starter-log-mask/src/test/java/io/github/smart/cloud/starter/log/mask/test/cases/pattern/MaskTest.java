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
package io.github.smart.cloud.starter.log.mask.test.cases.pattern;

import io.github.smart.cloud.starter.log.mask.pattern.util.PatternUtil;
import io.github.smart.cloud.starter.log.mask.test.prepare.App;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MaskTest {

    @Test
    public void testAll() {
        String c1 = "address：深圳市龙岗区布吉";
        String mask1 = PatternUtil.mask(c1);
        System.out.println(mask1);
        Assertions.assertThat(mask1).isEqualTo("address：***");

        String c2 = "地址：深圳市龙岗区布吉";
        String mask2 = PatternUtil.mask(c2);
        System.out.println(mask2);
        Assertions.assertThat(mask2).isEqualTo("地址：***");

        String c3 = "信息系，name：张三";
        String mask3 = PatternUtil.mask(c3);
        System.out.println(mask3);
        Assertions.assertThat(mask3).isEqualTo("信息系，name：***");
    }

}