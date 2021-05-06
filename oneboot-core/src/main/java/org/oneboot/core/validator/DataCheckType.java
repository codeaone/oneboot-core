package org.oneboot.core.validator;

public interface DataCheckType {

	/**
	 * 检查数据格式是否合法
	 * 
	 * @param value
	 * @return
	 */
	boolean check(String value);
}
