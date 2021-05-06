package org.oneboot.core.result;

import org.oneboot.core.logging.ToString;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 针对web mvc 端的返回对像
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@Getter
@Setter
@Accessors(chain = true)
public class CommonMvcResult<T> extends ToString {

	/** serialVersionUID **/
	private static final long serialVersionUID = 942917567059741136L;

	/** 是代表业务是否处理成功, 注意：我们对外的api只有一个状态：业务处理成功， 没有 接口调用成功 的状态 */
	private boolean success = false;

	/** 业务代码 **/
	private String code;

	/** 业务返回数据 **/
	private T data;

	/** 调用说明 **/
	private Object message;

	/** 更清晰的错误信息 **/
	private Object errors;

	/** 异常反馈：0：tost，1: 弹窗 **/
	private Integer showType = 0;

	/** 签名信息 */
	private String sign;
}
