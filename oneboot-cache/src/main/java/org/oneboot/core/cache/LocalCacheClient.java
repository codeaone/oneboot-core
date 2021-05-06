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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 本地缓存，方便分布式缓存不可用时，本地联调方便
 * 
 * @author tushiqiao
 * @version $Id: LocalCacheClient.java, v 0.1 2017年5月23日 下午1:26:24 tushiqiao Exp $
 */
public class LocalCacheClient implements CacheClient {

    /** 本地缓存 */
    private static ConcurrentMap<String, Object> localMap = new ConcurrentHashMap<String, Object>();

    private static ConcurrentMap<String, LinkedList<Object>> stackMap = new ConcurrentHashMap<String, LinkedList<Object>>();

    @Override
    public boolean putWithExpire(String key, Object value, int expire) {
        localMap.put(key, value);
        return true;
    }

    @Override
    public Object get(String key) {
        return localMap.get(key);
    }

    @Override
    public boolean remove(String key) {
        localMap.remove(key);
        return true;
    }

    @Override
    public boolean exists(String key) {
        return localMap.containsKey(key);
    }

    @Override
    public boolean pushStack(String queueName, Object value) {
        if (stackMap.get(queueName) == null) {
            stackMap.put(queueName, new LinkedList<>());
        }
        stackMap.get(queueName).addFirst(value);
        return true;
    }

    @Override
    public Object popStack(String queueName) {
        if (stackMap.get(queueName) == null) {
            stackMap.put(queueName, new LinkedList<>());
        }
        try {
            return stackMap.get(queueName).removeLast();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public Long lookStackLength(String queueName) {
        if (stackMap.get(queueName) == null) {
            stackMap.put(queueName, new LinkedList<>());
        }
        return stackMap.get(queueName).size() * 1L;
    }

    @Override
    public long incr(String key, long delta) {
        return 0;
    }

    @Override
    public long decr(String key, long delta) {
        return 0;
    }

    @Override
    public List<Object> popStack(String queueName, long size) {
        return null;
    }

    @Override
    public boolean pushStack(String queueName, List<Object> value) {
        return false;
    }

	@Override
	public boolean refreshTimeout(String key, int time) {
		// TODO Auto-generated method stub
		return false;
	}

}
