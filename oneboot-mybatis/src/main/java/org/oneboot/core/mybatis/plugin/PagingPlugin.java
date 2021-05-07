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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.oneboot.core.mybatis.model.BaseSearcher;
import org.oneboot.core.mybatis.model.BaseTotalSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将拦截Executor.prepare()方法实现分页方言的插入.
 *
 * @author shiqiao.pro
 * 
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class PagingPlugin implements Interceptor {
    private static Logger logger = LoggerFactory.getLogger(PagingPlugin.class);

    /** 数据库方言 **/
    private String dialect = "";
    /** mapper.xml中需要拦截的ID(正则匹配) **/
    private String pageSqlId = "";

    /** 处理SQL **/
    protected CountSqlParser countSqlParser = new CountSqlParser();

    /**
     * 将sql转换为总记录数SQL
     * 
     * @param sql
     *            SQL语句
     * @return 总记录数的sql
     */
    public String getCountString(String sql) {
        try {
            return countSqlParser.getSmartCountSql(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "select count(1) from (" + sql + ") tmp_count";
    }

    @Override
    public Object intercept(Invocation ivk) throws Throwable {
        if (ivk.getTarget() instanceof RoutingStatementHandler) {
            RoutingStatementHandler statementHandler = (RoutingStatementHandler) ivk.getTarget();
            BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler,
                    "delegate");
            MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate,
                    "mappedStatement");

            if (mappedStatement.getId().matches(pageSqlId)) {
                // 拦截需要分页的SQL
                BoundSql boundSql = delegate.getBoundSql();
                Object parameterObject = boundSql.getParameterObject();
                // 分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
                if (parameterObject == null) {
                    throw new NullPointerException("parameterObject尚未实例化！");
                } else {
                    Connection connection = (Connection) ivk.getArgs()[0];
                    String sql = boundSql.getSql();
                    // String countSql = "select count(0) from (" + sql+ ") as
                    // tmp_count"; //记录统计
                    String countSql = getCountString(sql);
                    // String countSql = "select count(0) from (" + sql+ ")
                    // tmp_count";
                    // 记录统计 == oracle 加 as 报错(SQL command not properly ended)
                    PreparedStatement countStmt = connection.prepareStatement(countSql);
                    BoundSql countBs = new BoundSql(mappedStatement.getConfiguration(), countSql,
                            boundSql.getParameterMappings(), parameterObject);
                    setParameters(countStmt, mappedStatement, countBs, parameterObject);
                    ResultSet rs = countStmt.executeQuery();
                    int count = 0;
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                    rs.close();
                    countStmt.close();

                    if (parameterObject instanceof BaseTotalSearcher) {
                        BaseTotalSearcher page = (BaseTotalSearcher) parameterObject;
                        page.setTotalCount(count);
                        String pageSql = genPageWithTotalResultSql(sql, page);
                        ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
                        // 将分页sql语句反射回BoundSql.
                    } else if (parameterObject instanceof BaseSearcher) {
                        // 参数就是Page实体
                        BaseSearcher page = (BaseSearcher) parameterObject;
                        String pageSql = genPageWithoutTotalResultSql(sql, page);
                        ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql);
                        // 将分页sql语句反射回BoundSql.
                    }
                }
            }
        }
        return ivk.proceed();
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * 
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws java.sql.SQLException
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void setParameters(PreparedStatement ps, MappedStatement mappedStatement, BoundSql boundSql,
            Object parameterObject) throws SQLException {
        ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings != null) {
            Configuration configuration = mappedStatement.getConfiguration();
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            MetaObject metaObject = parameterObject == null ? null : configuration.newMetaObject(parameterObject);
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();
                    PropertyTokenizer prop = new PropertyTokenizer(propertyName);
                    if (parameterObject == null) {
                        value = null;
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                        value = parameterObject;
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName);
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)
                            && boundSql.hasAdditionalParameter(prop.getName())) {
                        value = boundSql.getAdditionalParameter(prop.getName());
                        if (value != null) {
                            value = configuration.newMetaObject(value)
                                    .getValue(propertyName.substring(prop.getName().length()));
                        }
                    } else {
                        value = metaObject == null ? null : metaObject.getValue(propertyName);
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    if (typeHandler == null) {
                        throw new ExecutorException("There was no TypeHandler found for parameter " + propertyName
                                + " of statement " + mappedStatement.getId());
                    }
                    typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
                }
            }
        }
    }

    /**
     * 根据数据库方言，生成特定的分页sql，不含有记录总条数结果。 适用于海量数据的查询。
     * 
     * @param sql
     * @param page
     * @return
     */
    private String genPageWithoutTotalResultSql(String sql, BaseSearcher page) {
        if (page != null && StringUtils.isNotBlank(dialect) && !"null".equals(dialect)) {
            StringBuffer pageSql = new StringBuffer();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + page.getFirst() + "," + page.getPageSize());
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    /**
     * 根据数据库方言，生成特定的分页sql,含有记录总条数的返回，适用于数据量不多的查询
     *
     * @param sql
     * @param page
     * @return
     */
    private String genPageWithTotalResultSql(String sql, BaseTotalSearcher page) {
        if (page != null && StringUtils.isNotBlank(dialect) && !"null".equals(dialect)) {
            StringBuffer pageSql = new StringBuffer();
            if ("mysql".equals(dialect)) {
                pageSql.append(sql);
                pageSql.append(" limit " + page.getFirst() + "," + page.getPageSize());
            }
            return pageSql.toString();
        } else {
            return sql;
        }
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties p) {
        dialect = p.getProperty("dialect");
        if (StringUtils.isEmpty(dialect) || "null".equals(dialect)) {
            try {
                throw new PropertyException("dialect property is not found!");
            } catch (PropertyException e) {
                logger.error(e.getMessage(), e);
            }
        }
        pageSqlId = p.getProperty("pageSqlId");
        if (StringUtils.isEmpty(pageSqlId) || "null".equals(pageSqlId)) {
            try {
                throw new PropertyException("pageSqlId property is not found!");
            } catch (PropertyException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getPageSqlId() {
        return pageSqlId;
    }

    public void setPageSqlId(String pageSqlId) {
        this.pageSqlId = pageSqlId;
    }

}
