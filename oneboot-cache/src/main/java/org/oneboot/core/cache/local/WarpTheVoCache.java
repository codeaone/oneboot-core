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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.oneboot.core.enums.EnumObject;
import org.springframework.util.CollectionUtils;

/**
 * 本地缓存使用
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class WarpTheVoCache {
	/** 读写锁 */
	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

	private static final Map<String, List<EnumObject>> CACHE = new HashMap<>();
	private static final Map<String, Map<String, EnumObject>> CACHE_MAP = new HashMap<>();

	/**
	 * 
	 * @param key：
	 *            voiceFile， merchant， appInfo，voicesmsTask
	 * @param param
	 * @return
	 */
	public static List<EnumObject> get(String key, WarpTheVoService service) {
		// lock.writeLock().unlock();
		List<EnumObject> eoList = null;
		boolean falg = false;
		Lock readlock = LOCK.readLock();
		readlock.lock();
		try {
			eoList = CACHE.get(key);

			if (CollectionUtils.isEmpty(eoList)) {
				eoList = service.loadingData(key);
				falg = true;
			}

		} finally {
			readlock.unlock();
		}

		if (falg && !CollectionUtils.isEmpty(eoList)) {
			WarpTheVoCache.putData(key, eoList);
		}

		return eoList;

	}

	public static Map<String, EnumObject> getMap(String key, WarpTheVoService service) {
		boolean falg = false;
		Map<String, EnumObject> eoMap;
		Lock readlock = LOCK.readLock();
		readlock.lock();
		try {
			eoMap = CACHE_MAP.get(key);
			if (CollectionUtils.isEmpty(eoMap)) {
				eoMap = service.loadingDataMap(key);
				falg = true;
			}

		} finally {
			readlock.unlock();
		}

		if (falg && !CollectionUtils.isEmpty(eoMap)) {
			WarpTheVoCache.putDataMap(key, eoMap);
		}

		return eoMap;

	}

	public static void putDataMap(String key, Map<String, EnumObject> map) {
		Lock writelock = LOCK.writeLock();
		writelock.lock();
		try {
			CACHE_MAP.put(key, map);
		} finally {
			writelock.unlock();
		}

	}

	public static void putData(String key, List<EnumObject> list) {
		Lock writelock = LOCK.writeLock();
		writelock.lock();
		try {
			CACHE.put(key, list);
		} finally {
			writelock.unlock();
		}
	}

	/**
	 * 清除所有的数据
	 */
	public static void clear(String key) {
		Lock writelock = LOCK.writeLock();
		writelock.lock();

		try {
			CACHE.remove(key);
			CACHE_MAP.remove(key);
		} finally {
			writelock.unlock();
		}
	}

	/**
	 * 清除所有的数据
	 */
	public static void clearAll() {
		Lock writelock = LOCK.writeLock();
		writelock.lock();

		try {
			CACHE.clear();
			CACHE_MAP.clear();
		} finally {
			writelock.unlock();
		}
	}
}
