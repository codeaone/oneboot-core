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
package org.oneboot.core.logging;

import org.oneboot.core.context.ServiceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

/**
 * 方便logger实现的小工具.
 * 
 * @author shiqiao.pro
 * 
 */
public class LoggerUtil {

    /** 线程编号修饰符 */
    /*
     * private final static char THREAD_RIGHT_TAG = ']';
     * 
     *//** 线程编号修饰符 *//*
                      * private static final char THREAD_LEFT_TAG = '[';
                      */

    /** 换行符 */
    public static final char LINE_BREAK = '\n';

    /** 换行符 */
    public static final char UNDER_SCORE = '_';

    /** 逗号 */
    public static final char COMMA = ',';

    /** 日志前缀 */
    public static final String LOG_PREFIX = "invokeId";

    public static Logger getLogger() {
        Logger logger = ServiceContextHolder.getLogger();
        if (null == logger) {
            logger = LoggerFactory.getLogger("DEFAULT");
        }
        return logger;
    }

    /**
     * 输出info level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void info(Logger logger, String message, Object... params) {
        if (logger.isInfoEnabled()) {
            logger.info(format(message, params));
        }
    }

    public static void info(Logger logger, String message) {
        if (logger.isInfoEnabled()) {
            logger.info(format("{0}", message));
        }
    }

    /**
     * 输出info level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void info(String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isInfoEnabled()) {
            logger.info(format(message, params));
        }
    }

    /**
     * 输出warn level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void warn(Logger logger, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params));
        }
    }

    /**
     * 输出warn level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void warn(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params), throwable);
        }
    }

    /**
     * 输出warn level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void warn(String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params));
        }
    }

    /**
     * 输出warn level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void warn(Throwable throwable, String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isWarnEnabled()) {
            logger.warn(format(message, params), throwable);
        }
    }

    /**
     * 输出debug level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void debug(Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params));
        }
    }

    /**
     * 输出debug level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void debug(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params), throwable);
        }
    }

    /**
     * 输出debug level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void debug(String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params));
        }
    }

    /**
     * 输出debug level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void debug(Throwable throwable, String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug(format(message, params), throwable);
        }
    }

    /**
     * 输出error level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void error(Logger logger, String message, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params));
        }
    }

    /**
     * 输出error level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void error(Throwable throwable, Logger logger, String message, Object... params) {
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params), throwable);
        }
    }

    /**
     * 输出error level的log信息.
     * 
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void error(String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params));
        }
    }

    /**
     * 输出error level的log信息.
     * 
     * @param throwable
     *            异常对象
     * @param logger
     *            日志记录器
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     */
    public static void error(Throwable throwable, String message, Object... params) {
        Logger logger = getLogger();
        if (logger.isErrorEnabled()) {
            logger.error(format(message, params), throwable);
        }
    }

    /**
     * 日志信息参数化格式化
     * 
     * @param message
     *            log信息,如:<code>xxx{0},xxx{1}...</code>
     * @param params
     *            log格式化参数,数组length与message参数化个数相同, 如:<code>Object[]  object=new Object[]{"xxx","xxx"}</code>
     * @return 格式化后的日志信息
     */
    public static String format(String message, Object... params) {
    	
    	return MessageFormatter.arrayFormat(message, params).getMessage();
       /* if (params != null && params.length != 0) {
            return tranceInfoFormat(MessageUtil.formatMessage(message, params));
        } else {
            return tranceInfoFormat(message);
        }*/

    }

}
