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
package org.oneboot.core.context;

import org.oneboot.core.constant.LoggerConstants;
import org.slf4j.Logger;

/**
 * 线程上下文
 * 
 * @author shiqiao.pro
 * 
 */
public class ServiceContextHolder {

	/** 线程日志 */
	private static ThreadLocal<Logger> logger = new ThreadLocal<Logger>();

	/** 分页off */
	private static ThreadLocal<Integer> offSet = new ThreadLocal<Integer>();
	/** 一页大小 */
	private static ThreadLocal<Integer> pageSize = new ThreadLocal<Integer>();

	/** 排序字段 */
	private static ThreadLocal<String> sortField = new ThreadLocal<String>();
	/**  */
	private static ThreadLocal<String> sortOrder = new ThreadLocal<String>();

	static {
		logger.set(LoggerConstants.DEFAULT_LOGGER);
	}

	public static String getSortField() {
		return sortField.get();
	}

	public static void setSortField(String sortField) {
		ServiceContextHolder.sortField.set(sortField);
	}

	public static String getSortOrder() {
		return sortOrder.get();
	}

	public static void setSortOrder(String sortOrder) {
		ServiceContextHolder.sortOrder.set(sortOrder);
	}

	public static int getOffSet() {
		Integer os = offSet.get();
		if (os == null) {
			return 0;
		}
		return os;
	}

	public static void setOffSet(Integer offSet) {
		ServiceContextHolder.offSet.set(offSet);
	}

	/**
	 * removeOffSet
	 */
	public static void removeOffSet() {
		offSet.remove();
	}

	public static int getPageSize() {
		Integer ps = pageSize.get();
		if (ps == null) {
			return 10;
		}
		return ps;
	}

	public static void setPageSize(Integer pageSize) {
		ServiceContextHolder.pageSize.set(pageSize);
	}

	public static Logger getLogger() {
		return logger.get();
	}

	public static void setLogger(Logger logger) {
		// LogHolder.setLogger(logger);
		ServiceContextHolder.logger.set(logger);
	}

	/**
	 * 清除上下文信息
	 */
	public static void clean() {
		ServiceContextHolder.logger.remove();
		ServiceContextHolder.logger.set(LoggerConstants.DEFAULT_LOGGER);
		ServiceContextHolder.offSet.remove();
		ServiceContextHolder.pageSize.remove();
		ServiceContextHolder.sortField.remove();
		ServiceContextHolder.sortOrder.remove();
	}

}
