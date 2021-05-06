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
package org.oneboot.core.cache.manager;

/**
 * 缓存管理者服务接口，由后台管理调用实现所有缓存的刷新
 * 
 * @author tushiqiao
 * @version $Id: CacheManagerService.java, v 0.1 2017年5月13日 下午9:15:14 tushiqiao Exp $
 */
interface CacheManagerService {
    /**
     * 刷新全部缓存
     */
    void refreshAllCache();

    /**
     * 刷新特定的缓存
     * 
     * @param cacheName
     * @return 刷新是否有异常
     */
    boolean refreshCache(ICacheName cacheName);

    /**
     * 按照指定的渠道系统刷新特定的缓存
     * 
     * @param cacheName
     * @param channelSystemId
     * @return
     */
    boolean refreshByChannelSystem(ICacheName cacheName, String channelSystemId);

    /**
     * 打印全部缓存
     */
    void dumpAllCache();

    /**
     * 打印特定的缓存
     * 
     * @param cacheName
     */
    void dumpCache(ICacheName cacheName);

    /**
     * 打印渠道全部缓存
     */
    void dumpByChannelSystem(String channelSystem);
}
