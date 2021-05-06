package org.oneboot.core.logging.sensitive.types;

import org.oneboot.core.logging.sensitive.SensitiveDataUtil;

/**
 * 个人名字
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class PersonNameType extends AbstractType {

	@Override
	String doShield(Object value, String[] additions) {
		String tmp = value.toString().trim();
		int showLastCharNum = tmp.length() - 1;
		return SensitiveDataUtil.customizeHide(value.toString(), 0, showLastCharNum, 1);

	}

}
