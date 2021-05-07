package org.oneboot.core.exception;

import org.oneboot.core.error.ErrorCode;
import org.oneboot.core.error.ErrorContext;
import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.utils.MessageUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * OneBoot 统一异常
 * 
 * @author shiqiao.pro
 * 
 */
@Setter
@Getter
public class ObootException extends RuntimeException {

	/** serialVersionUID **/
	private static final long serialVersionUID = -9219282094540925862L;

	/** 结果码 */
	private IErrorCode errorCode = CommonErrorCode.SYSTEM_ERROR;

	/** 额外的异常信息 */
	private String errorDesc;

	/** 客户端提示类型，根据客户端和业务需要可任意扩展。0:toast、1:弹框 */
	private Integer showType;

	/** 错误上下文对象 */
	private ErrorContext errorContext;

	public static void main(String[] args) {
		ObootException e = new ObootException(CommonErrorCode.RPC_ERROR);
		System.out.println(e);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorSpecificCode 异常的业务错误类型
	 * @param cause             异常的 cause {@link #getCause()}
	 */
	public ObootException(Throwable cause, IErrorCode errorCode, String message, Object... arguments) {
		super(MessageUtil.formatMessage(message, arguments), cause);
		this.errorCode = errorCode;
		defErrorContext(errorCode);
	}

	public ObootException(IErrorCode errorCode, String message, Object... arguments) {
		super(MessageUtil.formatMessage(message, arguments));
		this.errorCode = errorCode;
		defErrorContext(errorCode);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode 错误码
	 */
	public ObootException(IErrorCode errorCode) {
		super();
		this.errorCode = errorCode;
		defErrorContext(errorCode);

	}

	private void defErrorContext(IErrorCode errorCode) {
		this.errorContext = new ErrorContext();
		ErrorCode ec = new ErrorCode(errorCode.getErrorLevel(), errorCode.getErrorType(), errorCode.getCode());
		CommonError error = new CommonError(ec, errorCode.getDesc(), "oneboot");
		this.errorContext.addError(error);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode
	 * @param e
	 */
	public ObootException(IErrorCode errorCode, Exception e) {
		super(e);
		this.errorCode = errorCode;
		if (e instanceof ObootException) {
			this.errorContext = ((ObootException) e).getErrorContext();
		} else {
			defErrorContext(errorCode);
		}
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode
	 * @param e
	 */
	public ObootException(IErrorCode errorCode, int showType, Exception e) {
		super(e);
		this.errorCode = errorCode;
		this.showType = showType;
		defErrorContext(errorCode);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode
	 * @param e
	 */
	public ObootException(IErrorCode errorCode, String errorDesc, Exception e) {
		super(e);
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		defErrorContext(errorCode);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode 错误码
	 * @param showType  异常信息
	 */
	public ObootException(IErrorCode errorCode, String errorDesc) {
		super();
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		defErrorContext(errorCode);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode    错误码
	 * @param showType     异常信息
	 * @param errorContext
	 */
	public ObootException(IErrorCode errorCode, int showType, ErrorContext errorContext) {
		super();
		this.errorCode = errorCode;
		this.errorContext = errorContext;
		this.showType = showType;
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode 错误码
	 * @param errorDesc 异常信息
	 * @param showType  客户端提示类型
	 */
	public ObootException(IErrorCode errorCode, Integer showType) {
		super();
		this.errorCode = errorCode;
		this.showType = showType;
		defErrorContext(errorCode);
	}

	/**
	 * 构造函数
	 * 
	 * @param errorCode 错误码
	 * @param errorDesc 异常信息
	 * @param showType  客户端提示类型
	 */
	public ObootException(IErrorCode errorCode, String errorDesc, Integer showType) {
		super();
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
		this.showType = showType;
		defErrorContext(errorCode);
	}

	public ObootException(IErrorCode errorCode, ErrorContext errorContext) {
		super();
		this.errorCode = errorCode;
		this.errorContext = errorContext;
	}

	/**
	 * 重写getMessage，附带结果枚举的信息
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder(200);
		if (super.getMessage() != null) {
			sb.append(super.getMessage());
		}
		sb.append(" 异常原因：");
		sb.append(this.getErrorCode().getCode());
		sb.append("|" + this.getErrorCode().getDesc());
		sb.append("|" + this.getErrorCode().getView());

		if (StringUtils.isNotBlank(errorDesc)) {
			sb.append("|");
			sb.append(errorDesc);
		}
		if (this.errorContext != null) {
			sb.append("|");
			sb.append(errorContext.toString());
		}
		return sb.toString();
	}

}
