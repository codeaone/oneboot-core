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
package org.oneboot.core.exception;

/**
 * 异常代码接口
 * 
 * @author shiqiao.pro
 * 
 */
public interface IErrorCode {

    /**
     * 获取错误代码
     * 
     * @return
     */
    public String getCode();

    /**
     * 描述，更针对程序错误原因
     * 
     * @return
     */
    public String getDesc();

    /**
     * 显示错误内容，更针对客的显示文案
     * 
     * @return
     */
    public String getView();

    /**
     * errorLevel ${ @link ErrorLevels}
     * 
     * @return
     */
    public String getErrorLevel();

    /**
     * { @link ErrorTypes}
     * 
     * @return
     */
    public String getErrorType();

}
