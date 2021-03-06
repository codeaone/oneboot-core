package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveDataUtil;

/**
 * 银行卡
 * 
 * @author shiqiao.pro
 * 
 */
public class BankCardNoType extends AbstractType {

	@Override
	String doShield(Object value, String[] additions) {
		return SensitiveDataUtil.bankCardNoHide(value.toString());
	}

}
