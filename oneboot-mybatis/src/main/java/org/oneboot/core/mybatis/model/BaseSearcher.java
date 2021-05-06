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
package org.oneboot.core.mybatis.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.oneboot.core.context.ServiceContextHolder;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.logging.ToString;

import com.google.common.base.CaseFormat;

/**
 * 当结果数据量很大时，我们不需要查询出记录总条数
 *
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class BaseSearcher extends ToString {
	/**  */
	private static final long serialVersionUID = -2535602346077165831L;

	/** 默认值 */
	public static final int DEFAULT_PAGE_NO = 1;
	/** 默认值 */
	public static final int DEFAULT_PAGE_SIZE = 10;

	/** 当前页数 */
	private int pageNo = 1;

	/** 每页条数 */
	private int pageSize = 10;

	/** 排序字段 */
	private String sortField;

	/**  */
	private String sortOrder;
	/** 数据权限 **/
	private DataScopeData dataScope;

	/** 排序字段映射，因为从前端传过来的不一定是DB对应的字段 */
	public Map<String, String> sotrFieldMap = new HashMap<>();

	public void putFieldMap(String key, String value) {
		sotrFieldMap.put(key, value);
	}

	public String getLowerCamel(String name) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
	}

	private String getSortFieldByDeclared(String sortField) {
		// 通过反射，拿到所有的字段，再进行匹配
		Class<?> cls = this.getClass();
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			if (StringUtils.equals(sortField, field.getName())) {
				return getLowerCamel(sortField);
			}
		}
		return null;
	}

	/**
	 * 设置查询默认参数
	 */
	public void setDefaultParam() {
		if (getPageNo() == DEFAULT_PAGE_NO) {
			this.setPageNo(ServiceContextHolder.getOffSet());
		}
		if (getPageSize() == DEFAULT_PAGE_SIZE) {
			LoggerUtil.debug("PageSize: {}", ServiceContextHolder.getPageSize());
			this.setPageSize(ServiceContextHolder.getPageSize());
		}

		String sortField = ServiceContextHolder.getSortField();
		if (StringUtils.isNoneBlank(sortField)) {
			// 先从sotrFieldMap中拿，拿到就直接使用
			String sf = sotrFieldMap.get(sortField);
			if (StringUtils.isBlank(sf)) {

				sf = getSortFieldByDeclared(sortField);
				if (StringUtils.isBlank(sf)) {
					// 要去掉Name再试一下
					sf = getSortFieldByDeclared(StringUtils.substringBeforeLast(sortField, "Name"));
				}
			}

			// 设置自动排序字段信息
			if (StringUtils.isNotBlank(sf) && StringUtils.isNotBlank(ServiceContextHolder.getSortOrder())) {

				this.setSortField(sf);
				this.setSortOrder(ServiceContextHolder.getSortOrder());
			} else {
				LoggerUtil.warn("SortField={0} 字段未配置，该字段不生效。请到SearchContext中配置好", sortField);
				this.setSortField(null);
				this.setSortOrder(null);
			}
		} else {
			this.setSortField(null);
			this.setSortOrder(null);
		}
	}

	public BaseSearcher() {

	}

	public BaseSearcher(int pageNo, int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	public DataScopeData getDataScope() {
		return dataScope;
	}

	public void setDataScope(DataScopeData dataScope) {
		this.dataScope = dataScope;
	}

	/**
	 * Getter method for property <tt>pageNo</tt>.
	 * 
	 * @return property value of pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * Setter method for property <tt>pageNo</tt>.
	 * 
	 * @param pageNo
	 *            value to be assigned to property pageNo
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * Getter method for property <tt>pageSize</tt>.
	 * 
	 * @return property value of pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * Getter method for property <tt>sortField</tt>.
	 * 
	 * @return property value of sortField
	 */
	public String getSortField() {
		return sortField;
	}

	/**
	 * Setter method for property <tt>sortField</tt>.
	 * 
	 * @param sortField
	 *            value to be assigned to property sortField
	 */
	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	/**
	 * Getter method for property <tt>sortOrder</tt>.
	 * 
	 * @return property value of sortOrder
	 */
	public String getSortOrder() {
		return sortOrder;
	}

	/**
	 * Setter method for property <tt>sortOrder</tt>.
	 * 
	 * @param sortOrder
	 *            value to be assigned to property sortOrder
	 */
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	/**
	 * Setter method for property <tt>pageSize</tt>.
	 * 
	 * @param pageSize
	 *            value to be assigned to property pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getFirst() {
		if (pageNo < 1) {
			pageNo = 1;
		}
		return ((pageNo - 1) * pageSize);
	}

}
