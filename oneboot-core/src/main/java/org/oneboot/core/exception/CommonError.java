package org.oneboot.core.exception;

import java.io.Serializable;

import org.oneboot.core.error.ErrorCode;

import lombok.Getter;
import lombok.Setter;

/**
 * 标准错误对象。
 * 
 * <p>
 * 标准错误对象包含:
 * <ul>
 * <li>标准错误码</li>
 * <li>错误默认文案</li>
 * <li>错误产生位置</li>
 * </ul>
 * <p>
 * 标准错误对象是一次错误处理结果的描述。
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
public class CommonError implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = 2778298944139324817L;

	/** 错误编码 */
	private ErrorCode errorCode;

	/** 错误描述 */
	private String errorMsg;

	/** 错误发生系统 */
	private String location;

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return errorCode + "@" + location + ":" + errorMsg;
	}

	public CommonError(ErrorCode errorCode, String errorMsg, String location) {
		super();
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
		this.location = location;
	}

	/**
	 * 默认构造方法
	 */
	public CommonError() {
		super();
	}

}
