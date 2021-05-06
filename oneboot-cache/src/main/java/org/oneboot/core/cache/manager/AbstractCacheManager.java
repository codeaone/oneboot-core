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

import java.util.Observable;

import org.oneboot.core.logging.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存管理抽象类
 * 
 * @author tushiqiao
 * @version $Id: AbstractCacheManager.java, v 0.1 2016年8月24日 下午9:20:00 tushiqiao Exp $
 */
public abstract class AbstractCacheManager implements CacheManager {

    /** logger */
    protected static final Logger logger = LoggerFactory.getLogger("CACHE");

    /** 
     */
    @Override
    public boolean initCache() {
        String cacheNameDesc = getCacheName().getDesc();
        LoggerUtil.info("开始初始化{0}", cacheNameDesc);
        try {
            loadingCache();
            return true;
        } catch (Exception e) {
            LoggerUtil.error(e, "初始化{0}出现异常", cacheNameDesc);
            return false;
        } finally {
            LoggerUtil.info("{0}初始化结束", cacheNameDesc);
        }
    }

    /** 
     */
    @Override
    public boolean refreshCache() {
        String cacheNameDesc = getCacheName().getDesc();
        LoggerUtil.info(logger, "开始刷新{0}", cacheNameDesc);
        try {
            loadingCache();
            afterRefresh();
            return true;
        } catch (Exception e) {
            LoggerUtil.error(e, "刷新{0}出现异常", cacheNameDesc);
            return false;
        } finally {
            LoggerUtil.info(logger, "{0}刷新结束", cacheNameDesc);
        }
    }

    /** 
     */
    @Override
    public boolean refreshByChannelSystemId(String channelSystemId) {
        // 如果子类没覆盖这个方法，则表示不需要实现按渠道刷新
        return refreshCache();
    }

    /** 
     */
    @Override
    public void dump() {
        String cacheNameDesc = getCacheName().getDesc();
        LoggerUtil.info(logger, "========开始打印{0}========\n{1}", cacheNameDesc, getCacheInfo());
        LoggerUtil.info(logger, "========打印{0}结束========", cacheNameDesc);
    }

    /** 
     */
    @Override
    public void dump(String channelSystem) {
        String cacheNameDesc = getCacheName().getDesc();
        LoggerUtil.info(logger, "========开始打印渠道{0}缓存{1}========\n{2}", channelSystem, cacheNameDesc,
                getCacheInfo(channelSystem));
        LoggerUtil.info(logger, "========打印渠道{0}缓存{1}结束========", channelSystem, cacheNameDesc);
    }

    /**
     * 刷新之后，其他业务处理，比如监听器的实现
     */
    protected void afterRefresh() {
        // 有需要后续动作的缓存实现
    }

    /**
     * 获取缓存信息，用于日志打印操作
     * 
     * @return
     */
    protected abstract String getCacheInfo();

    /**
     * 用于打印缓存操作
     * 
     * @param channelSystem
     * @return
     */
    protected String getCacheInfo(String channelSystem) {
        return "TODO";
    }

    /**
     * 缓存加载，从数据库中加载
     */
    protected abstract void loadingCache();

    @Override
    public void afterPropertiesSet() throws Exception {
        CacheManagerRegistryImpl.registerServerManager(getCacheName(), this);
    }

    @Override
    public void update(Observable o, Object arg) {
    }

    @Override
    public void initialize() {
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
