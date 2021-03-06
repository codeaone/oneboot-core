/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oneboot.core.mvc.file;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 
 * @author shiqiao.pro
 * 
 */
@Component
public class MultiFileValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MultiFileBucket.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        MultiFileBucket multiBucket = (MultiFileBucket) obj;

        int index = 0;

        for (FileBucket file : multiBucket.getFiles()) {
            if (file.getFile() != null) {
                if (file.getFile().getSize() == 0) {
                    errors.rejectValue("files[" + index + "].file", "missing.file");
                }
            }
            index++;
        }

    }
}
