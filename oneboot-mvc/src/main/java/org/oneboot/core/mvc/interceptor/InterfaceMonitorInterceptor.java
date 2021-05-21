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
package org.oneboot.core.mvc.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.oneboot.core.context.ServiceContextHolder;
import org.oneboot.core.logging.LoggerUtil;
import org.oneboot.core.logging.annotations.DigestName;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class InterfaceMonitorInterceptor implements HandlerInterceptor {

	private NamedThreadLocal<Long> startDatetimeMSThreadLocal = new NamedThreadLocal<>("adminMonitor.startMS");

	public String printRequestParam(HttpServletRequest request) {
		Enumeration<String> paramNames = request.getParameterNames();
		StringBuffer paramsSb = new StringBuffer("");
		String paramName;
		while (paramNames.hasMoreElements()) {
			paramName = paramNames.nextElement();
			paramsSb.append(paramName + ": " + request.getParameter(paramName));
			paramsSb.append(", ");
		}

		LoggerUtil.info("接到请求参数: {}", paramsSb);
		return paramsSb.toString();
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		startDatetimeMSThreadLocal.set(System.currentTimeMillis());
		setSearchContext(request);
		if (handler instanceof HandlerMethod) {
			HandlerMethod handlerMethod = (HandlerMethod) handler;

			DigestName dlog = handlerMethod.getBeanType().getAnnotation(DigestName.class);
			if (dlog != null && StringUtils.isNotBlank(dlog.value())) {
				ServiceContextHolder.setLogger(LoggerFactory.getLogger(dlog.value()));
			}

		}
		return true;
	}

	/**
	 * 设置查询上下文信息
	 * 
	 * @param request
	 */
	private void setSearchContext(HttpServletRequest request) {
		ServiceContextHolder.setOffSet(getOffSet(request));
		ServiceContextHolder.setPageSize(getPageSize(request));

		String sortField = request.getParameter("sortField");
		String sortOrder = request.getParameter("sortOrder");
		if (StringUtils.isNotBlank(sortField) && StringUtils.isNotBlank(sortOrder)) {
			ServiceContextHolder.setSortField(sortField);
			if (StringUtils.equals("ascend", sortOrder)) {
				ServiceContextHolder.setSortOrder("asc");
			} else {
				ServiceContextHolder.setSortOrder("desc");
			}
		} else {
			ServiceContextHolder.getSortField();
			ServiceContextHolder.getSortOrder();
		}
	}

	/**
	 * 获得pager.offset参数的值
	 * 
	 * @param request
	 * @return
	 */
	protected int getOffSet(HttpServletRequest request) {
		int offSet = 0;
		try {
			String pageOff = request.getParameter("page");
			if (pageOff != null) {
				offSet = Integer.parseInt(pageOff);
			} else {
				pageOff = request.getParameter("current");
				if (pageOff != null) {
					offSet = Integer.parseInt(pageOff);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return offSet;
	}

	protected int getPageSize(HttpServletRequest request) {
		int offSet = 20;
		try {
			String pageSize = request.getParameter("pageSize");
			if (pageSize != null) {
				offSet = Integer.parseInt(pageSize);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return offSet;
	}


	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long costMS = System.currentTimeMillis() - startDatetimeMSThreadLocal.get();
		saveAccessTrace(request, costMS);
		ServiceContextHolder.clean();
	}

	/**
	 * access trace log record
	 *
	 * @param request
	 */
	private void saveAccessTrace(HttpServletRequest request, long visitCostMS) {
	}

}
