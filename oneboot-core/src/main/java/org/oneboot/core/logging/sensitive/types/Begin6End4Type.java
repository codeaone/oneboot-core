package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveDataUtil;

/**
 * 前3后4
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class Begin6End4Type extends AbstractType {

	@Override
	String doShield(Object value, String[] additions) {
		int length = value.toString().trim().length();
		return SensitiveDataUtil.customizeHide(value.toString(), 6, 4, length - 10);

	}

}
