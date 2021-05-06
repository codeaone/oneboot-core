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

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * 
 * @author shiqiao.pro
 * @see <a href="https://boot.codeaone.com">codeaone</a>
 */
public class RedisCacheClient implements CacheClient {
	private static Logger logger = LoggerFactory.getLogger(RedisCacheClient.class);

	// @Inject
	/** 应用名字 */
	private String appName;

	private final static String KEY_PREFIX = "SMART_PLATFORM";

	private RedisTemplate<String, Object> redisTemplate;

	/** ValueOperations 理解成Map<Object,Object> **/
	private final ValueOperations<String, Object> valueOps;

	/** ListOperations可以理解为List<Object> 可以用来做队列使用 进栈出栈 **/
	private final ListOperations<String, Object> listOps;

	/**
	 * @param template
	 */
	@Autowired
	public RedisCacheClient(RedisTemplate<String, Object> template) {
		super();
		this.redisTemplate = template;
		this.valueOps = redisTemplate.opsForValue();
		this.listOps = redisTemplate.opsForList();
	}

	/**
	 * 进栈
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public boolean pushStack(String key, Object value) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("set value is null");
		}
		try {
			// 左边进，也就是头部
			listOps.leftPush(getKey(key), value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;

	}

	@Override
	public boolean pushStack(String key, List<Object> value) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("set value is null");
		}
		try {
			// 左边进，也就是头部
			listOps.leftPushAll(getKey(key), value);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 出栈
	 * 
	 * @param key
	 * @return
	 */
	@Override
	public Object popStack(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		// 右边出，也就是尾部出，先进先出
		return listOps.rightPop(getKey(key));
	}

	@Override
	public List<Object> popStack(String key, long size) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (size < -1) {
			throw new IllegalArgumentException("size 必须大于 -1");
		}
		return listOps.range(getKey(key), 0, size);
	}

	@Override
	public Long lookStackLength(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		return listOps.size(getKey(key));
	}

	/**
	 * 返回符合项目要求的key
	 * 
	 * @param key
	 * @return
	 */
	private String getKey(final String key) {
		if (StringUtils.isBlank(appName)) {
			throw new IllegalArgumentException("appName is null!");
		}
		return KEY_PREFIX + "." + appName + "." + key;
	}

	@Override
	public boolean putWithExpire(final String key, final Object value, final int expire) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("set value is null");
		}

		try {

			valueOps.set(getKey(key), value);
			// 如果为0，不设置过期时间。
			if (expire > 0) {
				redisTemplate.expire(getKey(key), expire, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Object get(final String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		return valueOps.get(getKey(key));
	}

	@Override
	public boolean exists(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		return redisTemplate.hasKey(getKey(key));
	}

	@Override
	public boolean remove(final String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}

		try {
			redisTemplate.delete(getKey(key));
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Setter method for property <tt>appName</tt>.
	 * 
	 * @param appName
	 *            value to be assigned to property appName
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public long incr(String key, long delta) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (delta < 0) {
			throw new IllegalArgumentException("递增因子必须大于0");
		}
		return valueOps.increment(getKey(key), delta);
	}

	@Override
	public long decr(String key, long delta) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (delta < 0) {
			throw new IllegalArgumentException("递增因子必须大于0");
		}
		return valueOps.increment(getKey(key), -delta);
	}

	@Override
	public boolean refreshTimeout(String key, int time) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}

		try {
			if (time > 0) {
				redisTemplate.expire(getKey(key), time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

}
