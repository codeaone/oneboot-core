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

/**
 * 缓存客户端
 * @author shiqiao.pro
 * 
 */
public interface CacheClient {
    /**
     * 把一个对象放入cache,并设置过期时间。
     * 
     * <p>
     * 注：value的大小要小于64K
     * </p>
     * 
     * @param key
     *            保证Cache服务器中的唯一性。 <code>Not Null</code>
     * @param value
     *            保存的值。 <code>Not Null</code>
     * @param expire
     *            过期时间(单位为秒)，0 表示长期 <code>Not Null</code>
     * @return 保存成功, 返回<code>true</code>, 否则, 返回<code>false</code>
     */
    boolean putWithExpire(String key, Object value, int expire);


    /**
     * 取出一个对象
     * 
     * @param key
     *            保证Cache服务器中的唯一性。 <code>Not Null</code>
     * @return 获取的对象
     */
    Object get(String key);

    /**
     * 删除一个对象
     * 
     * @param key
     *            <code>Not Null</code>
     * @return 删除成功, 返回<code>true</code>, 否则, 返回<code>false</code>
     */
    boolean remove(String key);

    /**
     * 判断缓存中key是否存在着，如果是新增，有必要做此操作，如果是更新，则不需要，自己在业务上控制好。
     * 
     * @param key
     * @return
     */
    boolean exists(final String key);

    /**
     * 进栈 先进先出
     * 
     * @param queueName
     *            队列名
     * @param value
     * @return 保存成功, 返回<code>true</code>, 否则, 返回<code>false</code>
     */
    boolean pushStack(String queueName, Object value);

    boolean pushStack(String queueName, List<Object> value);

    /**
     * 出栈 先进先出
     * 
     * @param queueName
     *            队列名
     * @return
     */
    Object popStack(String queueName);

    /**
     * 获取list缓存的内容
     * 
     * @param queueName
     * @param size
     *            结束 0 到 -1代表所有值
     * @return
     */
    List<Object> popStack(String queueName, long size);

    /**
     * 查看队列长度
     * 
     * @param queueName
     * @return
     */
    Long lookStackLength(String queueName);

    /**
     * 递增
     * 
     * @param key
     * @param delta
     *            要增加几(大于0)
     * @return
     */
    long incr(String key, long delta);

    /**
     * 递减
     * 
     * @param key
     * @param delta
     * @return
     */
    long decr(String key, long delta);
    
    /**
	 * 刷新过期时间
	 * @param key
	 * @param time
	 * @return
	 */
	boolean refreshTimeout(String key, int time);
}
