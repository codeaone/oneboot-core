package org.oneboot.core.validator;

/**
 * 参数检查
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface ParamValidate {

	/**
	 * 数据格式检查
	 * 
	 * @param param 被校验参数
	 */
	void validate(Object param);
}
