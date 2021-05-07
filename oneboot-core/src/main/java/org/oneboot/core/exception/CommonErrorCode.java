package org.oneboot.core.exception;

import org.oneboot.core.error.ErrorLevel;
import org.oneboot.core.error.ErrorType;
import org.oneboot.core.lang.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用错误码<br/>
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements IErrorCode {
	/** 处理成功 */
	SUCCESS("001", "处理成功", "处理成功", ErrorLevel.INFO, ErrorType.BIZ),

	/** 未知异常 */
	UNKNOWN_ERROR("002", "未知异常", "呃，出错了，请联系管理员", ErrorLevel.WARN, ErrorType.BIZ),

	/** 非法参数 */
	ILLEGAL_PARAMETERS("003", "非法参数", "请检查下你输入的参数是否正确哦~", ErrorLevel.INFO, ErrorType.BIZ),

	/** 系统错误 */
	SYSTEM_ERROR("004", "系统错误", "呃，出错了，请联系管理员", ErrorLevel.ERROR, ErrorType.SYSTEM),

	/** 重复提交错误 */
	REPEATED_SUBMIT("005", "重复提交", "请勿重复提交", ErrorLevel.WARN, ErrorType.BIZ),

	/** 非法操作 */
	ILLEGAL_OPERATION("006", "非法操作", "非法操作哦！", ErrorLevel.WARN, ErrorType.BIZ),

	/** 服务调用异常 */
	RPC_ERROR("010", "服务调用异常", "服务调用异常", ErrorLevel.ERROR, ErrorType.SYSTEM),

	/** 数据查询失败 */
	DATA_SELECT_FAIL("012", "DATA_SELECT_FAIL", "数据查询失败 ", ErrorLevel.INFO, ErrorType.BIZ),

	/** 数据检查失败 */
	DATA_CHCEK_FAIL("013", "DATA_CHCEK_FAIL", "数据检查失败 ", ErrorLevel.INFO, ErrorType.BIZ),

	/** 数据被占用 */
	DATA_CHCEK_EXIST("01４", "DATA_CHCEK_EXIST", "数据被占用 ", ErrorLevel.ERROR, ErrorType.BIZ),

	/** session过期 */
	SESSION_EXPIRED("015", "SESSION_EXPIRED", "会话过期", ErrorLevel.INFO, ErrorType.BIZ),;

	;
	/** 结果码 */
	private String code;

	/** 描述 */
	private String desc;

	/** 显示错误内容 */
	private String view;

	/** errorLevel */
	private String errorLevel;

	/** errorType */
	private String errorType;

	/**
	 * 通过name获取结果码
	 * 
	 * @param code
	 *            错误码
	 * @return 返回业务结果码
	 */
	public static CommonErrorCode getResultEnum(String code) {
		for (CommonErrorCode result : values()) {
			if (StringUtils.equals(result.getCode(), code)) {
				return result;
			}
		}
		return null;
	}

}
