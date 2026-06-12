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
package io.github.smart.cloud.starter.monitor.api.test.prepare.controller.slow;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("product")
public class ProductController {

    @GetMapping("query")
    public ResponseEntity<String> query(int i) throws InterruptedException {
        if (i % 2 == 0) {
            TimeUnit.MILLISECONDS.sleep(101);
        }
        return ResponseEntity.ok("success1");
    }

    @PostMapping("update")
    public ResponseEntity<String> update(int i) throws InterruptedException {
        if (i % 2 == 0) {
            TimeUnit.MILLISECONDS.sleep(101);
        }
        return ResponseEntity.ok("success2");
    }

    @PostMapping("reduce")
    public ResponseEntity<String> reduce(int i) throws InterruptedException {
        if (i % 2 == 0) {
            TimeUnit.MILLISECONDS.sleep(101);
        }
        return ResponseEntity.ok("success2");
    }

    @PostMapping("whitelist")
    public ResponseEntity<String> whitelist() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(101);
        return ResponseEntity.ok("success2");
    }

}