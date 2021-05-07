package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveType;

/**
 * 屏蔽实现的抽象类，提供公共方法，并且将实现类与外部隔离
 * 
 * @author shiqiao.pro
 * 
 */
public abstract class AbstractType implements SensitiveType {
	/**
	 * 是否清除全部，默认为false
	 * 
	 * @return
	 */
	public boolean isClean() {
		return false;
	}

	/**
	 * 默认情况直接返回null
	 * 
	 * @return
	 */
	public String handleNull() {
		return null;
	}

	/**
	 * 默认情况直接返回空串
	 * 
	 * @return
	 */
	public String handleEmpty() {
		return "";
	}

	public String shield(Object value, String[] additions) {
		// 统一处理null和""情况
		if (value == null) {
			return handleNull();
		}
		if (value.toString().isEmpty()) {
			return handleEmpty();
		}
		return doShield(value, additions);
	}

	abstract String doShield(Object value, String[] additions);
}
