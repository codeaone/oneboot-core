package org.oneboot.core.cache.util;

public class Constant {
	/** KEY分隔符 */
	public static final char KEY_SAPERATOR = '-';

	/**
	 * 组装KEY值，默认使用'-'作为分割符号
	 * 
	 * @param inputs
	 * @return
	 */
	public static String getKey(String... inputs) {
		return getKey(KEY_SAPERATOR, inputs);
	}

	/**
	 * 组装KEY值
	 * 
	 * @param saperator
	 * @param inputs
	 * @return
	 */
	public static String getKey(char saperator, String... inputs) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < inputs.length; i++) {
			String input = inputs[i];
			result.append(input);

			if (i != inputs.length - 1) {
				result.append(saperator);
			}
		}
		return result.toString();
	}
}
