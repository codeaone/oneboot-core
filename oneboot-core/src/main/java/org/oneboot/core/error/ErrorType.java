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
package org.oneboot.core.error;

/**
 * 错误类型常量
 * 
 * @author shiqiao.pro
 * 
 */
public interface ErrorType {

    /** 系统错误 */
    public static final String SYSTEM = "0";

    /** 业务错误 */
    public static final String BIZ = "1";

    /** 第三方错误 */
    public static final String THIRD_PARTY = "2";
}
