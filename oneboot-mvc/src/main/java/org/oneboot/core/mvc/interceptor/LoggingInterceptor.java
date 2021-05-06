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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oneboot.core.logging.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoggingInterceptor implements HandlerInterceptor {
	protected static final Logger logger = LoggerFactory.getLogger("WEB-DIGEST");

	private NamedThreadLocal<Long> startDatetimeMSThreadLocal = new NamedThreadLocal<>("LoggingInterceptor");

	private void printInfo(final HttpServletRequest request, final HttpServletResponse response, Long currentTime,
			Object handler) {
		if (logger.isInfoEnabled()) {
			if (handler instanceof HandlerMethod) {
				HandlerMethod hm = (HandlerMethod) handler;
				String daoFullName = hm.getMethod().getDeclaringClass().getSimpleName();

				StringBuilder msg = new StringBuilder();
				msg.append("(");
				if (request.getMethod() != null) {
					msg.append(request.getMethod()).append(" ");
				}
				msg.append(request.getRequestURI()).append("-->").append(daoFullName).append('.')
						.append(hm.getMethod().getName()).append(",");
				msg.append(System.currentTimeMillis() - currentTime).append("ms)");
				LoggerUtil.info(logger, "{}", msg.toString());
			}

		}
	}

	/** 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		startDatetimeMSThreadLocal.set(System.currentTimeMillis());
		return true;
	}

	/** 
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/** 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		printInfo(request, response, startDatetimeMSThreadLocal.get(), handler);
	}

}
