package org.oneboot.core.result;

import org.oneboot.core.error.ErrorContext;
import org.oneboot.core.exception.CommonError;
import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * CommonRpcResult 为 Result 的构造方法增加默认的初始化 RPC
 * 接口返回类务必继承此类，主要是会把错误上下文信息返回到调用系统，更方便去排查问题 当一个业务失败了，通过异常信息来区别异常类型，好做对应的逻辑处理
 * 
 * @author shiqiao.pro
 * 
 */
@Getter
@Setter
@Accessors(chain = true)
public class CommonRpcResult extends ToString {

	/** serialVersionUID **/
	private static final long serialVersionUID = 3111890011644811191L;

	/** 是否成功 */
	private boolean success = true;

	/** 错误上下文 */
	private ErrorContext errorContext;

	/**
	 * 先判断errorContext是否存在，为errorContext分配内存，再在errorStack中增加一项Error
	 * 
	 * @param error
	 *            带增加的Error
	 */
	public void addError(CommonError error) {
		ErrorContext errorContext = fillErrorContext();
		errorContext.addError(error);
	}

	/**
	 * 先判断errorContext是否存在，为errorContext分配内存，再将第三方Error填写到errorContext中
	 * 
	 * @param thirdPartError
	 *            第三方错误类型
	 */
	public void setThirdPartyError(String thirdPartError) {
		ErrorContext errorContext = fillErrorContext();
		errorContext.setThirdPartyError(thirdPartError);
	}

	/**
	 * 判断当前ErrorContext是否存在，不存在则创建一个ErrorContext
	 * 
	 * @return 当前Result的ErrorContext
	 */
	private ErrorContext fillErrorContext() {
		ErrorContext errorContext = this.getErrorContext();
		if (null == errorContext) {
			errorContext = new ErrorContext();
			this.setErrorContext(errorContext);
		}
		return errorContext;
	}
}
