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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.oneboot.core.logging.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * 缓存管理者注册处，集中缓存管理者
 * 
 * @author tushiqiao
 * @version $Id: CacheManagerRegistryImpl.java, v 0.1 2016年8月30日 下午2:12:47 tushiqiao Exp $
 */
@Service
public class CacheManagerRegistryImpl implements CacheManagerRegistry, Initializable {

    /** logger */
    private static final Logger logger = LoggerFactory.getLogger(CacheManagerRegistryImpl.class);

    /** 缓存管理组件名称 */
    // private static final String POINT_NAME = "cache";

    /** 缓存管理器列表 */
    private static Map<ICacheName, CacheManager> manager = new HashMap<ICacheName, CacheManager>();

    /** 
     */
    @Override
    public void refreshAllCache() {
        Set<Entry<ICacheName, CacheManager>> entrySet = manager.entrySet();
        for (Entry<ICacheName, CacheManager> entry : entrySet) {
            entry.getValue().refreshCache();
        }
    }

    /** 
     */
    @Override
    public void dumpAllCache() {
        Set<Entry<ICacheName, CacheManager>> entrySet = manager.entrySet();
        for (Entry<ICacheName, CacheManager> entry : entrySet) {

            try {
                entry.getValue().dump();
            } catch (Exception e) {
                LoggerUtil.error(e, "缓存打印出现异常,cacheName={0}", entry.getKey());

                // 缓存打印不是很高级别的事件，打印出现异常可以忽略的，至少不要影响服务器启动
                continue;
            }
        }
    }

    /** 
     */
    @Override
    public boolean refreshCache(ICacheName cacheName) {
        CacheManager cacheManager = manager.get(cacheName);
        if (cacheManager == null) {
            LoggerUtil.warn(logger, "没有注册缓存管理器，cacheName={0}", cacheName);
            return false;
        }

        try {
            return cacheManager.refreshCache();
        } finally {
            cacheManager.dump();
        }
    }

    /**
     * @see com.huanwu.eboot.cache.manager.huanwu.sp.opengw.common.service.facade.cache.CacheManagerRegistry#refreshCache(com.huanwu.eboot.cache.manager.huanwu.sp.opengw.common.service.facade.config.cache.CacheName,
     *      java.lang.String)
     */
    @Override
    public boolean refreshCache(ICacheName cacheName, String channelSystemId) {
        CacheManager cacheManager = manager.get(cacheName);
        if (cacheManager == null) {
            LoggerUtil.warn(logger, "没有注册缓存管理器，cacheName={0}", cacheName);
            return false;
        }

        try {
            return cacheManager.refreshByChannelSystemId(channelSystemId);
        } finally {
            cacheManager.dump();
        }
    }

    @Override
    public void dumpCache(ICacheName cacheName) {
        CacheManager cacheManager = manager.get(cacheName);
        if (cacheManager == null) {
            LoggerUtil.warn(logger, "没有注册缓存管理器，cacheName={0}", cacheName);
            return;
        }

        cacheManager.dump();
    }

    /**
     * dump渠道所有缓存
     */
    @Override
    public void dumpAllCache(String channelSystem) {
        Set<Entry<ICacheName, CacheManager>> entrySet = manager.entrySet();
        for (Entry<ICacheName, CacheManager> entry : entrySet) {

            try {
                entry.getValue().dump(channelSystem);
            } catch (Exception e) {
                LoggerUtil.error(e, "缓存打印出现异常,cacheName={0}", entry.getKey());

                // 缓存打印不是很高级别的事件，打印出现异常可以忽略的，至少不要影响服务器启动
                continue;
            }
        }
    }

    @Override
    public void initialize() {
//        LogHolder.setLogger(LoggerFactory.getLogger("INIT-CACHE"));
        LoggerUtil.info("单机缓存初始化。。。");
//        Profiler.enter("单机缓存初始化");
        {
            Set<Entry<ICacheName, CacheManager>> entrySet = manager.entrySet();
            for (Entry<ICacheName, CacheManager> entry : entrySet) {
                entry.getValue().initCache();
            }
        }
//        Profiler.release();

//        Profiler.enter("打印单机缓存信息");
        {
            dumpAllCache();
        }
//        Profiler.release();
        LoggerUtil.info("单机缓存初始化完成。。。");
    }

    /**
     * 注册缓存客户端
     * 
     * @param cacheName
     * @param cacheManager
     */
    public static void registerServerManager(ICacheName cacheName, CacheManager cacheManager) {
        Collections.synchronizedMap(manager).put(cacheName, cacheManager);
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        InstantiationTracingBeanPostProcessor.registerServerManager(this);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
