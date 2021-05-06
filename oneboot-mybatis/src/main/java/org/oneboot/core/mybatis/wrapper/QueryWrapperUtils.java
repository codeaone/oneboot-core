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
package org.oneboot.core.mybatis.wrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.mybatis.model.BaseSearcher;
import org.oneboot.core.mybatis.model.DataScopeData;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.CaseFormat;

public class QueryWrapperUtils {

	/**
	 * 转换成数据库字段
	 * 
	 * @param name
	 * @return
	 */
	public static String getLowerCamel(String name) {
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
	}

	/**
	 * 获取字段的get方法
	 * 
	 * @param methods
	 * @param fieldName
	 * @return
	 */
	private static Method getFieldGetMethod(Method[] methods, String fieldName) {
		String getName = "get" + getFirstUpperCase(fieldName);
		for (Method method : methods) {
			if (StringUtils.equals(getName, method.getName())) {
				return method;
			}
		}
		return null;
	}

	private static Object getFieldValue(BaseSearcher bs, String fieldName) {
		Method[] methods = bs.getClass().getDeclaredMethods();
		Method getMethod = getFieldGetMethod(methods, fieldName);
		if (getMethod == null) {
			return null;
		}
		Object value = null;
		try {
			value = getMethod.invoke(bs);
		} catch (Exception e) {
			LoggerUtil.warn("getQueryParams error {}, {}", fieldName, e.getMessage());
		}
		return value;
	}

	/**
	 * 首字母大写
	 * 
	 * @param name
	 * @return
	 */
	private static String getFirstUpperCase(String name) {
		String first = name.substring(0, 1).toUpperCase();
		String rest = name.substring(1, name.length());
		String newStr = new StringBuffer(first).append(rest).toString();
		return newStr;
	}

	public static Map<String, Object> getColumnMap(Map<String, Object> columnMap) {
		Map<String, Object> map = new HashMap<>(16);
		for (String key : columnMap.keySet()) {
			if (columnMap.get(key) != null) {
				map.put(getLowerCamel(key), columnMap.get(key));
			}
		}

		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setMapWrapper(QueryWrapper wrapper, Map<String, Object> columnMap) {
		for (String key : columnMap.keySet()) {
			if (columnMap.get(key) != null) {
				wrapper.eq(getLowerCamel(key), columnMap.get(key));
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setQueryWrapper(QueryWrapper wrapper, BaseSearcher bs) {

		DataScopeData data = bs.getDataScope();
		if (data != null) {
			if (data.isIn()) {
				wrapper.inSql(data.getFieldName(), data.getSql());
			} else {
				Map<String, Object> map = getQueryParams(bs);
				map.put(data.getFieldName(), data.getValue());
				wrapper.allEq(map, false);
			}
		} else {
			wrapper.allEq(getQueryParams(bs), false);
		}

		// 进行like字段处理
		Map<String, Object> likeMap = getQueryLikeParams(bs);
		for (String key : likeMap.keySet()) {
			if (likeMap.get(key) != null) {
				wrapper.like(getLowerCamel(key), likeMap.get(key));
			}
		}
		// 进行order by 处理
		if (StringUtils.isNotBlank(bs.getSortField())) {
			boolean isAsc = "asc".equals(bs.getSortOrder());
			wrapper.orderBy(true, isAsc, getLowerCamel(bs.getSortField()));
		} else {
			wrapper.orderByDesc("gmt_create");
		}
		// 进行时间段查询处理

	}

	private static Map<String, Object> getQueryLikeParams(BaseSearcher bs) {
		Map<String, Object> map = new HashMap<>(16);
		Class<?> cls = bs.getClass();
		// 获得某个类的所有声明的字段,但是不包括父类的申明字段。
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			String name = fieldName.toLowerCase();
			// like end start 这些过滤掉
			if (StringUtils.contains(name, "like")) {
				String fn = fieldName.replace("Like", "").replace("like", "");
				Object value = getFieldValue(bs, fieldName);
				if (value != null) {
					map.put(getLowerCamel(fn), value);
				}
			}
		}
		return map;
	}

	private static Map<String, Object> getQueryParams(BaseSearcher bs) {
		Map<String, Object> map = new HashMap<>(16);
		Class<?> cls = bs.getClass();
		// 获得某个类的所有声明的字段,但是不包括父类的申明字段。
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			String fieldName = field.getName();
			// like end start 这些过滤掉
			if (!filterQuery(fieldName)) {
				Object value = getFieldValue(bs, fieldName);
				if (value != null) {
					map.put(getLowerCamel(fieldName), value);
				}
			}
		}
		return map;
	}

	private static boolean filterQuery(String fieldName) {
		String name = fieldName.toLowerCase();
		return StringUtils.containsAny(name, "like", "end", "start");
	}

}
