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
package org.oneboot.core.mvc.base;

import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
public class BaseForm extends ToString {

	/**  */
	private static final long serialVersionUID = 6067554382512169769L;

	/** 需要批量修改的字段 */
	private String field;
	/** 需要批量修改值 */
	private String value;
	/** 上传文件的token */
	private String fileToken;
}
