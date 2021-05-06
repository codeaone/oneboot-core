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
 * 错误级别常量，主要用来做日志分析与打印
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface ErrorLevel {

    /** INFO级别 */
    public static final String INFO = "1";

    /** WARN级别 */
    public static final String WARN = "2";

    /** ERROR级别 */
    public static final String ERROR = "3";

    /** FATAL级别 */
    public static final String FATAL = "4";
}
