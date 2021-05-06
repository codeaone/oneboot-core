package org.oneboot.core.error;

import org.oneboot.core.exception.CommonError;
import org.oneboot.core.exception.CommonErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ErrorUtil {

	/** 普通日志 */
	private static final Logger logger = LoggerFactory.getLogger(ErrorUtil.class);

	/** 系统名称 */
	private static String appName;

	// ~~~ 构造方法

	private ErrorUtil() {
	}

	/**
	 * V1版本错误码构造函数。
	 * 
	 * @param errorLevel
	 *            错误级别。（1：信息、3：警告、5：错误、7：严重错误）
	 * @param errorType
	 *            错误类型。（0：系统错误、1：业务错误、2：第三方错误、9：未分类）
	 * @param errorScene
	 *            错误场景。（业务事件码）
	 * @param errorSpecific
	 *            错误编码。（错误明细编码，错误场景内唯一）
	 * @return 错误码实例。
	 */
	public static ErrorCode makeFcErrorCode(String errorLevel, String errorType, String errorScene,
			String errorSpecific) {
		return new ErrorCode(errorLevel, errorType, errorScene, errorSpecific);
	}

	/**
	 * V1版本错误码构造函数。<font color=red>支付宝 主站不要使用</font>
	 * 
	 * @param errorCode
	 *            错误码信息。
	 * @return 错误码实例。
	 */
	public static ErrorCode makeFcErrorCode(String errorCode) {
		// return new ErrorCode(errorCode, ErrorCodeVersion.VERSION_ONE);
		return new ErrorCode(errorCode);
	}

	public static ErrorCode makeFcErrorCode(String errorLevel, String errorType, String errorScene,
			String errorSpecific, String string2, String string22) {
		return new ErrorCode(errorLevel, errorType, errorScene, errorSpecific);
	}

	/**
	 * 创建ErrorCode
	 * 
	 * @param errorLevel
	 * @param errorType
	 * @param errorScene
	 * @param errorSpecific
	 * @return
	 */
	public static ErrorCode makeErrorCode(String errorLevel, String errorType, String errorScene,
			String errorSpecific) {
		return new ErrorCode(errorLevel, errorType, errorScene, errorSpecific);
	}

	/**
	 * 创建一个CommonError
	 * 
	 * @param errorLevel
	 * @param errorType
	 * @param errorScene
	 * @param errorSpecific
	 * @param message
	 * @return
	 */
	public static CommonError makeError(ErrorCode errorCode, String message) {

		CommonError error = new CommonError();
		error.setErrorCode(errorCode);
		error.setErrorMsg(message);
		error.setLocation(getAppName());
		return error;
	}

	/**
	 * 创建一个CommonError
	 * 
	 * @param errorLevel
	 * @param errorType
	 * @param errorScene
	 * @param errorSpecific
	 * @param message
	 * @param location
	 * @return
	 */
	public static CommonError makeError(ErrorCode errorCode, String message, String location) {

		CommonError error = new CommonError();
		error.setErrorCode(errorCode);
		error.setErrorMsg(message);
		error.setLocation(location);
		return error;
	}

	/**
	 * 增加一个error到errorContext中
	 * 
	 * @param error
	 * @return
	 */
	public static ErrorContext addError(CommonError error) {
		return addError(null, error);
	}

	/**
	 * 增加一个error到errorContext中
	 * 
	 * @param context
	 * @param error
	 * @return
	 */
	public static ErrorContext addError(ErrorContext context, CommonError error) {

		if (context == null) {
			context = new ErrorContext();
		}

		if (error == null) {

			logger.error("增加到ErrorContext中的CommonError不能为空");
			return context;
		}

		context.addError(error);

		return context;
	}

	/**
	 * 构建错误码
	 */
	/*
	 * public static String buildErrorCode(String sceneCode, ErrorEnumRule
	 * errorEnum) {
	 * 
	 * StringBuilder sb = new StringBuilder();
	 * 
	 * sb.append(errorEnum.getErrorLevel()).append(errorEnum.getErrorType());
	 * 
	 * sb.append(sceneCode).append(errorEnum.getCode());
	 * 
	 * return sb.toString(); }
	 */

	/**
	 * 创建并且增加一个Error到errorContext中
	 * 
	 * @param context
	 * @param errorCode
	 * @param message
	 * @return
	 */
	public static ErrorContext makeAndAddError(ErrorContext context, ErrorCode errorCode, String message) {

		CommonError error = makeError(errorCode, message);
		context = addError(context, error);

		return context;
	}

	/**
	 * 创建并且增加一个Error到新的errorContext中
	 * 
	 * @param errorCode
	 * @param message
	 * @return
	 */
	public static ErrorContext makeAndAddError(ErrorCode errorCode, String message) {

		CommonError error = makeError(errorCode, message);
		ErrorContext context = addError(error);

		return context;
	}

	/**
	 * 获取系统名称
	 * 
	 * @return
	 */
	public static String getAppName() {

		if (null == appName || "".equals(appName)) {
			return "unknown";
		}

		return appName;
	}

	/**
	 * Setter method for property <tt>appName</tt>.
	 * 
	 * @param appName
	 *            value to be assigned to property appName
	 */
	public void setAppName(String appName) {
		ErrorUtil.appName = appName;
	}

	public static CommonError makeError(String code, String message, String location) {
		ErrorCode errorCode = new ErrorCode();
		errorCode.setLevel(CommonErrorCode.ILLEGAL_PARAMETERS.getErrorLevel());
		errorCode.setType(CommonErrorCode.ILLEGAL_PARAMETERS.getErrorType());
		CommonError error = new CommonError();
		error.setErrorCode(errorCode);
		error.setErrorMsg(message);
		error.setLocation(location);
		return error;
	}
}
