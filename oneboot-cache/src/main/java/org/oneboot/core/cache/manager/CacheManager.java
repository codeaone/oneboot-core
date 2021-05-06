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

import java.util.Observer;

import org.springframework.beans.factory.InitializingBean;

/**
 * 缓存管理接口
 * 
 * 
 * @author tushiqiao
 * @version $Id: CacheManager.java, v 0.1 2017年5月13日 下午9:15:00 tushiqiao Exp $
 */
interface CacheManager extends Observer, Initializable, InitializingBean {

    /**
     * 初始化缓存
     * 
     * @return 初始化是否出现异常
     */
    boolean initCache();

    /**
     * 刷新缓存信息
     * 
     * @return 刷新是否出现异常
     */
    boolean refreshCache();

    /**
     * 按渠道刷新缓存
     * 
     * @param channelSystemId
     * @return
     */
    boolean refreshByChannelSystemId(String channelSystemId);

    /**
     * 获取缓存的名称
     * 
     * @return
     */
    ICacheName getCacheName();

    /**
     * 打印缓存信息
     */
    void dump();

    /**
     * 打印渠道缓存信息
     * 
     * @param channelSystem
     */
    void dump(String channelSystem);

}
