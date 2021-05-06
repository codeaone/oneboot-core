package org.oneboot.core.utils;

import java.util.Collection;
import java.util.Map;

import org.oneboot.core.exception.CommonErrorCode;
import org.oneboot.core.exception.ObootException;
import org.oneboot.core.lang.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * 常用断言工具类
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public abstract class Assert {

	/**
	 * 不能为空
	 * 
	 * @param str
	 * @param message
	 */
	public static void notBlank(final String str, String message) {
		if (StringUtils.isNoneBlank(str)) {
			throw new ObootException(CommonErrorCode.ILLEGAL_PARAMETERS, message);
		}
	}

	/**
	 * 不能为null
	 * 
	 * @param object
	 * @param message
	 */
	public static void notNull(@Nullable Object object, String message) {
		if (object == null) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL, message);
		}
	}

	/**
	 * 不能为空，必须有值
	 * 
	 * @param array
	 * @param message
	 */
	public static void notEmpty(@Nullable Object[] array, String message) {
		if (ObjectUtils.isEmpty(array)) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL, message);
		}
	}

	/**
	 * 不能为空，必须有值
	 * 
	 * @param collection
	 * @param message
	 */
	public static void notEmpty(@Nullable Collection<?> collection, String message) {
		if (CollectionUtils.isEmpty(collection)) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL, message);
		}
	}

	/**
	 * 不能为空，必须有值
	 * 
	 * @param map
	 * @param message
	 */
	public static void notEmpty(@Nullable Map<?, ?> map, String message) {
		if (CollectionUtils.isEmpty(map)) {
			throw new ObootException(CommonErrorCode.DATA_SELECT_FAIL, message);
		}
	}
}
