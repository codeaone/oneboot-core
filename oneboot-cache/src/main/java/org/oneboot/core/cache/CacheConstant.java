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
package org.oneboot.core.cache;

/**
 * 通用缓存常量
 * @author shiqiao.pro
 * 
 */
public interface CacheConstant {
	
	/** KEY分隔符 */
	public static final char KEY_SAPERATOR = '-';

	/**
	 * 组装KEY值，默认使用'-'作为分割符号
	 * 
	 * @param inputs
	 * @return
	 */
	public static String getKey(String... inputs) {
		return getKey(KEY_SAPERATOR, inputs);
	}

	/**
	 * 组装KEY值
	 * 
	 * @param saperator
	 * @param inputs
	 * @return
	 */
	public static String getKey(char saperator, String... inputs) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < inputs.length; i++) {
			String input = inputs[i];
			result.append(input);

			if (i != inputs.length - 1) {
				result.append(saperator);
			}
		}
		return result.toString();
	}

}
