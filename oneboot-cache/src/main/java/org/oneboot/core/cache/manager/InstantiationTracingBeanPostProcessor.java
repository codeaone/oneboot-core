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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.oneboot.core.logging.LoggerUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * 
 * @author tushiqiao
 * @version $Id: InstantiationTracingBeanPostProcessor.java, v 0.1 2016年9月26日 上午3:02:47 tushiqiao Exp $
 */
@Service
public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    private static List<Initializable> beans = new ArrayList<Initializable>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        LoggerUtil.info("这是一个什么事件呢？当一个ApplicationContext被初始化或刷新触发");
        if (event.getApplicationContext().getParent() == null) {
            // root application context 没有parent，他就是老大.
            // 需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法。
            LoggerUtil.info("哈哈，我是Boss");
            init();
        }
    }

    public static List<Initializable> getSortList() {
        // 按点击数倒序
        Collections.sort(beans, new Comparator<Initializable>() {
            public int compare(Initializable arg0, Initializable arg1) {
                int hits0 = arg0.getOrder();
                int hits1 = arg1.getOrder();
                if (hits1 < hits0) {
                    return 1;
                } else if (hits1 == hits0) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return beans;
    }

    /**
     * 初始化组件完成各个系统应用的初始化
     */
    private void init() {

        // 根据启动的级别进行排序
        // Collections.sort(beans);

        for (Initializable descriptor : getSortList()) {
            // for (Initializable descriptor : beans) {

            // 计时器
            StopWatch oneWatch = new StopWatch();
            oneWatch.start();

            try {
                descriptor.initialize();
            } catch (Throwable e) {
                // 某个bean初始化异常时，打印异常信息后继续初始化其他bean
                LoggerUtil.error(e, "初始化异常");
            }

            oneWatch.split();

            LoggerUtil.info("bean={} 初始化完成，耗时：{}ms", descriptor.getClass().getSimpleName(), oneWatch.getSplitTime());
        }
    }

    /**
     * @param cacheManager
     */
    public static void registerServerManager(Initializable cacheManager) {
        Collections.synchronizedList(beans).add(cacheManager);
    }
}
