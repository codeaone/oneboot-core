package org.oneboot.core.error;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.oneboot.core.exception.CommonError;

import lombok.Getter;
import lombok.Setter;

/**
 * 错误上下文对象。
 * <p>
 * 错误上下文对象包含标准错误对象的堆栈，和第三方错误信息。
 * </p>
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@Getter
@Setter
public class ErrorContext implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = -3553622585723714935L;

	/** 默认分隔符 */
	private static final String SPLIT = "|";

	/** 错误堆栈集合 */
	private List<CommonError> errorStack = new ArrayList<CommonError>();

	/** 第三方错误原始信息 */
	private String thirdPartyError;

	/**
	 * 获取当前错误对象
	 * 
	 * @return
	 */
	public CommonError fetchCurrentError() {
		if (errorStack != null && errorStack.size() > 0) {
			return errorStack.get(errorStack.size() - 1);
		}
		return null;
	}

	/**
	 * 获取原始错误对象
	 * 
	 * @return
	 */
	public CommonError fetchRootError() {
		if (errorStack != null && errorStack.size() > 0) {
			return errorStack.get(0);
		}
		return null;
	}

	/**
	 * 向堆栈中添加错误对象。
	 * 
	 * @param error
	 */
	public void addError(CommonError error) {
		if (errorStack == null) {
			errorStack = new ArrayList<CommonError>();
		}
		errorStack.add(error);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = errorStack.size(); i > 0; i--) {
			if (i == errorStack.size()) {
				sb.append(errorStack.get(i - 1));
			} else {
				sb.append(SPLIT).append(errorStack.get(i - 1));
			}
		}
		return sb.toString();
	}

}
