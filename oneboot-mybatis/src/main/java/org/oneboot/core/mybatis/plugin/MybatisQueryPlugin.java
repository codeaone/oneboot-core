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
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class }) })
public class MybatisQueryPlugin implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(MybatisQueryPlugin.class);

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        final Object[] args = ivk.getArgs();

        // ???????????????ms
        MappedStatement ms = (MappedStatement) args[0];

        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        Object paraObject = boundSql.getParameterObject();

        try {
            if (boundSql.getParameterObject() instanceof HashMap) {
                HashMap<String, Object> paramMap = (HashMap) boundSql.getParameterObject();
                // logger.warn("custcenter-MybatisQueryPlugin- DAO???paramMap???put????????????????????????" + TntInstUtil.getTntInstId());
                paramMap.put("tntInstId", TntInstUtil.getTntInstId());
            } else {
                // ??????????????????tntInstId????????????
                Class types[] = new Class[1];
                types[0] = Class.forName("java.lang.String");// ??????????????????????????????String
                if (paraObject == null) {
                    return ivk.proceed();
                }
                // ????????????????????????
                Class clazz = paraObject.getClass();
                try {
                    Method setTntInstId = clazz.getMethod("setTntInstId", types);
                    if (setTntInstId != null) {
                        // logger.warn("custcenter-MybatisQueryPlugin- ??????????????????????????????" + TntInstUtil.getTntInstId());
                        setTntInstId.invoke(paraObject, TntInstUtil.getTntInstId());
                    }
                } catch (NoSuchMethodException e) {
                    logger.debug("", e);
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return ivk.proceed();
    }

}
