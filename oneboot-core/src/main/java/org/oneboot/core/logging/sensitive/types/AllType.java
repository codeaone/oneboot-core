package org.oneboot.core.logging.sensitive.types;

/**
 * 全部屏蔽
 *
 * 约定：对于非String非空对象，返回空字符串"" 对于null或者空字符串，遵照ToStringUtil的约定，返回对象本身
 *
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class AllType extends AbstractType {

	@Override
	public String handleNull() {
		return "***";
	}

	@Override
	public String handleEmpty() {
		return "***";
	}

	String doShield(Object value, String[] addition) {
		return "***";
	}
}
