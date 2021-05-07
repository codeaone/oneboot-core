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
package org.oneboot.core.lang;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页数据存储
 * 
 * @param <T>
 * @author shiqiao.pro
 * 
 */
public class Page<T> implements Serializable {

	/**  */
	private static final long serialVersionUID = 7150020480639418931L;

	public static final int FIRST_PAGE = 1;

	protected int pageNo = FIRST_PAGE;
	protected int pageSize = 1;

	/** 返回结果 */
	protected List<T> result = new ArrayList<T>();

	/** 结果总条数 */
	protected int totalCount = -1;

	public Page() {
	}

	public Page(int pageNo) {
		this.pageSize = 10;
		this.pageNo = pageNo;
	}

	public Page(int pageNo, int pageSize) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
	}

	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	/**
	 * 返回Page对象自身的setPageNo函数,可用于连续设置。
	 */
	public Page<T> pageNo(final int thePageNo) {
		setPageNo(thePageNo);
		return this;
	}

	/**
	 * 获得每页显示条数
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 返回Page对象自身的setPageSize函数,可用于连续设置。
	 */
	public Page<T> pageSize(final int thePageSize) {
		setPageSize(thePageSize);
		return this;
	}

	/**
	 * 获得页内的记录列表.
	 */
	public List<T> getResult() {
		if (result == null) {
			return new ArrayList<T>();
		}
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List<T> result) {
		this.result = result;
	}

	/**
	 * 获得总记录数, 默认值为-1.
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 
	 * @return
	 */
	public int getTotalPageSize() {
		int page = 0;
		page = (int) (totalCount / pageSize);
		if (totalCount % pageSize > 0) {
			page++;
		}
		return page;
	}

}
