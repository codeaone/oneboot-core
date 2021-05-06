package org.oneboot.core.logging.sensitive;

import org.oneboot.core.logging.sensitive.types.AllType;
import org.oneboot.core.logging.sensitive.types.BankCardNoType;
import org.oneboot.core.logging.sensitive.types.Begin6End4Type;
import org.oneboot.core.logging.sensitive.types.CleanType;
import org.oneboot.core.logging.sensitive.types.IdCardNoType;
import org.oneboot.core.logging.sensitive.types.MobileNoType;
import org.oneboot.core.logging.sensitive.types.PersonNameType;

/**
 * 敏感数据屏蔽类型枚举
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public enum SensitiveTypeEnum {

	/** 什么都不输出 **/
	CLEAN("clean", CleanType.class),

	/** 屏蔽全部 **/
	ALL("all", AllType.class),

	/**
	 * 前6后4屏蔽
	 */
	BEGIN6_END4("begin6_end4", Begin6End4Type.class),

	/**
	 * 密码脱敏
	 */
	PASSWORD("password", AllType.class),

	/**
	 * 银行卡号脱敏
	 */
	BANK_CARD_NO("bank_card_no", BankCardNoType.class),

	/**
	 * 手机号脱敏
	 */
	MOBILE_NO("mobile_no", MobileNoType.class),

	/**
	 * 身份证号脱敏
	 */
	ID_CARD_NO("id_card_no", IdCardNoType.class),

	/**
	 * 客户姓名脱敏 隐藏第一个字
	 */
	PERSON_NAME("person_name", PersonNameType.class),

	;

	/** 方法 **/
	private String type;

	/** 需要实现的类 **/
	private Class<? extends SensitiveType> implementClass;

	/**
	 * 构造方法
	 */
	SensitiveTypeEnum(String type, Class<? extends SensitiveType> implementClass) {
		this.type = type;
		this.implementClass = implementClass;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Class<? extends SensitiveType> getImplementClass() {
		return implementClass;
	}

	public void setImplementClass(Class<? extends SensitiveType> implementClass) {
		this.implementClass = implementClass;
	}

}
