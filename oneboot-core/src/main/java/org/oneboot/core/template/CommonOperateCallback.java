package org.oneboot.core.template;

import org.oneboot.core.result.CommonRpcResult;
import org.springframework.transaction.TransactionStatus;

public abstract class CommonOperateCallback<T extends CommonRpcResult> {

	/**
	 * 操作前处理
	 */
	public abstract void before();

	/**
	 * 操作执行
	 * 
	 * @param result 处理结果
	 */
	public abstract void execute(T result);

	/**
	 * 事务模板中异常处理
	 * 
	 * @param t      异常
	 * @param status 事务状态
	 */
	public void processException(Throwable t, TransactionStatus status) {
		status.setRollbackOnly();
	}

	/**
	 * 事务后处理
	 */
	public void after(T result) {

	}
}
