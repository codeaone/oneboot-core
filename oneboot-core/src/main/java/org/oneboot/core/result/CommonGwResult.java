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
package org.oneboot.core.result;

import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * CommonGwResult 为 Result 的构造方法增加默认的初始化 针对网关的返回基类
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
public class CommonGwResult<T> extends ToString {

	/** serialVersionUID */
	private static final long serialVersionUID = -6397938553581787206L;

	/** 是代表业务是否处理成功, 注意：我们对外的api只有一个状态：业务处理成功， 没有 接口调用成功 的状态 */
	private boolean success = false;

	/** 业务代码 **/
	private String code;

	/** 业务返回数据 **/
	private T data;

	/** 调用说明 **/
	private Object message;

	/** 更清晰的错误信息 **/
	private Object errors;

	/** 异常反馈：0：tost，1: 弹窗 **/
	private Integer showType = 0;

	/** 签名信息 */
	private String sign;

}
