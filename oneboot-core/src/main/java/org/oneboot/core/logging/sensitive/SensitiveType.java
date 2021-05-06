package org.oneboot.core.logging.sensitive;

/**
 * 敏感数据过滤接口
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface SensitiveType {

	/**
	 * 隐藏符
	 */
	public static final char SHIELD_CHAR = '*';

	/**
	 * 隐藏敏感信息
	 * 
	 * @param value
	 *            需要屏蔽的值，一般是字符串
	 * @return
	 */
	public String shield(Object value, String[] addition);

	/**
	 * 是否返回空串
	 * 
	 * @return
	 */
	public boolean isClean();
}
