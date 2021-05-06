package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveDataUtil;

/**
 * 居民身份证号码
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class IdCardNoType extends AbstractType {

	@Override
	String doShield(Object value, String[] additions) {
		return SensitiveDataUtil.idCardNoHide(value.toString());
	}

}
