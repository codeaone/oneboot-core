package org.oneboot.core.logging.sensitive.types;

/**
 * 什么都不输出
 *
 * @author shiqiao.pro
 * 
 */
public class CleanType extends AbstractType {

	String doShield(Object value, String[] addition) {
		return "";
	}

	@Override
	public boolean isClean() {
		return true;
	}

}
