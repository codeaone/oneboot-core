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

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

/**
 * 系统出现异常时，会发出此事件，用来上报异常行为
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
public class ErrorCodeEvent extends ApplicationEvent {

    /** serialVersionUID */
    private static final long serialVersionUID = -2785989239579599180L;

    public ErrorCodeEvent(Object source) {
        super(source);
    }

    public ErrorCodeEvent() {
        super("");
    }

    private IErrorCode errorCode;

    private Throwable error;

    /** 调用方法 */
    private String method;
    
    /** 错误描述 */
	private String errorMsg;

	/** 错误发生系统 */
	private String location;

}
