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
package org.oneboot.core.request;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 分页查询请求
 * 
 * @author shiqiao.pro
 * 
 */
@Setter
@Getter
@Accessors(chain = true)
public class PagedQueryRequest extends ToString {

	/**  */
	private static final long serialVersionUID = -836356730921326805L;

	/** ID **/
	private String id;

	/** 分页号，从第一页开始计算 */
	private Integer pageNo = 1;

	/** 每页条数 */
	@Min(1)
	@Max(1000)
	private Integer pageSize = 30;

	/** 字段名 */
	private String field;

	/** 字段值 */
	private String value;

	private List<String> idList;

}
