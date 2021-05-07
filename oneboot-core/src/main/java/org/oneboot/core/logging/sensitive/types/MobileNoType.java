package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveDataUtil;

/**
 * 手机号码
 * 
 * @author shiqiao.pro
 * 
 */
public class MobileNoType extends AbstractType {

	@Override
	String doShield(Object value, String[] additions) {
		 return SensitiveDataUtil.mobileHide(value.toString());
	}

}
