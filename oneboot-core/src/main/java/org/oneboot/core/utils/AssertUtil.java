package org.oneboot.core.utils;

import java.util.Collection;

import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.IErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.logging.LoggerUtil;
import org.springframework.util.CollectionUtils;

public final class AssertUtil {


    /**
     * 禁用构造函数
     */
    private AssertUtil() {
        // 禁用构造函数
    }

    /**
     * 校验list中必须有值
     * 
     * @param list
     * @param errorCode
     * @param message
     */
    public static <T> void isNotEmpty(Collection<T> list, IErrorCode errorCode, String message) {
        if (CollectionUtils.isEmpty(list)) {
            throwBizException(errorCode, message);
        }
    }

    /**
     * 期待对象为非空，如果检查的对象为<code>null</code>，抛出异常<code>ObootException</code>
     * 
     * @param object
     */
    public static void isNotNull(Object object) {
        if (object == null) {
            throwBizException(CommonErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 期待对象为非空，如果检查的对象为<code>null</code>，抛出异常<code>ObootException</code>
     * 
     * @param object
     * @param errorCode
     */
    public static void isNotNull(Object object, IErrorCode errorCode) {
        if (object == null) {
            throwBizException(errorCode);
        }
    }

    /**
     * 期待对象为非空，如果检查的对象为<code>null</code>，抛出异常<code>ObootException</code>
     * 
     * @param object
     * @param errorCode
     * @param message
     *            异常说明
     */
    public static void isNotNull(Object object, IErrorCode errorCode, String message) {
        if (object == null) {
            throwBizException(errorCode, message);
        }
    }

    /**
     * 
     * @param object
     * @param errorCode
     * @param message
     * @param params
     */
    public static void isNotNull(Object object, IErrorCode errorCode, String message, Object... params) {
        if (object == null) {
            LoggerUtil.warn(message, params);
            throwBizException(errorCode);
        }
    }

    /**
     * 期待字符串为非空，如果检查字符串是空白：<code>null</code>、空字符串""或只有空白字符，抛出异常<code>ObootException</code>
     * 
     * @param text
     *            待检查的字符串
     * @param errorCode
     *            异常代码
     */
    public static void isNotBlank(String text, IErrorCode errorCode) {
        if (StringUtils.isBlank(text)) {
            throwBizException(errorCode);
        }
    }

    public static void isNotBlank(String text, IErrorCode errorCode, String message, Object... params) {
        if (StringUtils.isBlank(text)) {
            LoggerUtil.warn(message, params);
            throwBizException(errorCode, message, params);
        }
    }

    /**
     * 判断String长度
     * 
     * @param text
     * @param length
     * @param message
     */
    public static void checkStringLength(String text, int length, IErrorCode errorCode, String message,
            Object... params) {
        isNotBlank(text, errorCode, message, params);
        if (text.length() != length) {
            throwBizException(errorCode, message, params);
        }
    }

    /**
     * 期待集合对象为非空，如果检查集合对象是否为null或者空数据，抛出异常<code>ObootException</code>
     * 
     * @param collection
     *            集合对象
     * @param errorCode
     *            异常代码
     */
    public static void notEmpty(Collection<?> collection, IErrorCode errorCode) {
        if (CollectionUtils.isEmpty(collection)) {
            throwBizException(errorCode);
        }
    }

    /**
     * 期待的正确值为true，如果实际为false，抛出异常<code>ObootException</code>
     * 
     * @param expression
     * @param errorCode
     *            异常代码
     */
    public static void isTrue(boolean expression, IErrorCode errorCode) {
        if (!expression) {
            throwBizException(errorCode);
        }
    }

    /**
     * 期待的正确值为false，如果实际为true，抛出异常<code>ObootException</code>
     * 
     * @param expression
     * @param errorCode
     *            异常代码
     * @throws ObootException
     */
    public static void isFalse(boolean expression, IErrorCode errorCode) {
        if (expression) {
            throwBizException(errorCode);
        }
    }

    /**
     * 期待的正确值为true，如果实际为false，抛出异常<code>ObootException</code>
     * 
     * @param expression
     *            表达式
     * @param errorCode
     *            错误代码
     * @param message
     *            异常说明
     */
    public static void isTrue(boolean expression, IErrorCode errorCode, String message) {
        if (!expression) {
            throwBizException(errorCode, message);
        }
    }

    /**
     * 期待的正确值为false，如果实际为true，抛出异常<code>ObootException</code>
     * 
     * @param expression
     *            表达式
     * @param errorCode
     *            错误代码
     * @param message
     *            异常说明
     */
    public static void isFalse(boolean expression, IErrorCode errorCode, String message) {
        if (expression) {
            throwBizException(errorCode, message);
        }
    }

    /**
     * 抛出异常
     * 
     * @param errorCode
     * @param message
     */
    public static void throwBizException(IErrorCode errorCode, String message) {
        throw new ObootException(errorCode, message);
    }

    /**
     * 抛出异常
     * 
     * @param errorCode
     * @param message
     */
    public static void throwBizException(IErrorCode errorCode) {
        throw new ObootException(errorCode);
    }

    /**
     * 抛出异常
     * 
     * @param errorCode
     * @param message
     */
    public static void throwBizException(IErrorCode errorCode, String message, Object... arguments) {
        throw new ObootException(errorCode, message, arguments);
    }
}
