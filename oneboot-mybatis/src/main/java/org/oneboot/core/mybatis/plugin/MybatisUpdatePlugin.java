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
package org.oneboot.core.mybatis.plugin;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MybatisUpdatePlugin implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(MybatisUpdatePlugin.class);

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        final Object[] args = ivk.getArgs();

        // 获取原始的ms
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        Object paraObject = boundSql.getParameterObject();
        // boundSql.setAdditionalParameter(name, value);
        try {
            if (paraObject instanceof HashMap) {
                HashMap<String, Object> paramMap = (HashMap) paraObject;
                paramMap.put("tntInstId", TntInstUtil.getTntInstId());
                // logger.warn("custcenter-MybatisUpdatePlugin-
                // DAO中paramMap中put设置，租户编号："
                // + TntInstUtil.getTntInstId());
            } else if (paraObject instanceof String) {
                logger.debug("paraObject={}", paraObject);
            } /*
               * else if (paraObject instanceof CombinateSequenceRange) { logger.debug("paraObject={}", paraObject); }
               */ else {
                // 通过反射，把tntInstId设置进去
                Class types[] = new Class[1];
                types[0] = Class.forName("java.lang.String");// 方法的参数对应下面的String
                // 对多租户进行设置
                Class clazz = paraObject.getClass();
                try {
                    Method setTntInstId = clazz.getMethod("setTntInstId", types);
                    // logger.warn("custcenter-MybatisUpdatePlugin- 反射设置，租户编号："
                    // + TntInstUtil.getTntInstId());
                    setTntInstId.invoke(paraObject, TntInstUtil.getTntInstId());
                } catch (Exception e) {
                    // TODO: handle exception
                }

                // 如果创建时间为空，就为当前时间
                Method getGmtCreate = clazz.getMethod("getGmtCreate");
                Object gmtCreate = getGmtCreate.invoke(paraObject);
                if (gmtCreate == null) {
                    types[0] = Class.forName("java.util.Date");
                    Method setGmtCreate = clazz.getMethod("setGmtCreate", types);
                    setGmtCreate.invoke(paraObject, new Date());
                }

                Method getGmtModified = clazz.getMethod("getGmtModified");
                Object gmtModified = getGmtModified.invoke(paraObject);
                if (gmtModified == null) {
                    types[0] = Class.forName("java.util.Date");
                    Method setGmtModified = clazz.getMethod("setGmtModified", types);
                    setGmtModified.invoke(paraObject, new Date());
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ivk.proceed();
    }

}
