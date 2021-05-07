package org.oneboot.core.error;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 标准错误码，方便异常日志系统有效识别问题
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
public class ErrorCode implements Serializable {

	/** serialVersionUID **/
	private static final long serialVersionUID = 6549108247694497326L;

	/** 错误级别,见<code>ErrorLevel</code>定义 */
	private String level = "1";

	/** 错误类型,见<code>ErrorType</code>定义 */
	private String type = "0";

	/** 错误场景 */
	private String scene = "0";

	/** 具体错误码 */
	private String specific = "0";

	/** 系统前缀 **/
	private String prefix = "";
	
	 /**
     * 默认构造方法
     */
    public ErrorCode() {
    }

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return prefix + type + level + scene + specific;
	}

	public ErrorCode(String level, String type, String scene, String specific, String prefix) {
		super();
		this.level = level;
		this.type = type;
		this.scene = scene;
		this.specific = specific;
		this.prefix = prefix;
	}

	public ErrorCode(String level, String type, String scene, String specific) {
		super();
		this.level = level;
		this.type = type;
		this.scene = scene;
		this.specific = specific;
	}

	public ErrorCode(String level, String type, String specific) {
		super();
		this.level = level;
		this.type = type;
		this.specific = specific;
	}

	public ErrorCode(String errorCode) {
		super();
	}

}
