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
package org.oneboot.core.cache.local;

import java.util.List;
import java.util.Map;

import org.oneboot.core.enums.EnumObject;

/**
 * 本地缓存实现服务
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface WarpTheVoService {

	/**
	 * 加载缓存数据
	 * 
	 * @param key
	 * @return
	 */
	List<EnumObject> loadingData(String key);

	/**
	 * 加载缓存数据
	 * 
	 * @param key
	 * @return
	 */
	Map<String, EnumObject> loadingDataMap(String key);

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public List<EnumObject> get(String key);

	/**
	 * 获取数据
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, EnumObject> getMap(String key);

	/**
	 * 当数据量太大时，放在内存中不合适，就需要时时去DB中查询数据 实现使用反谢的方式，有默认系统实现，如果不满足需求时，可以自行实现
	 * 
	 * @param result
	 *            需要转换的数据集合
	 * @param tableName
	 * @param tableField
	 * @param formField
	 * @param toField
	 */
	default void convertNameToDb(List<? extends Object> result, String tableName, String tableField, String formField,
			String toField) {

	}

}
