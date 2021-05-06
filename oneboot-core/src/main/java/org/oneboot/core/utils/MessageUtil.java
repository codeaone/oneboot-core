package org.oneboot.core.utils;

import java.text.MessageFormat;

public class MessageUtil {

	/**
	 * 使用<code>MessageFormat</code>格式化字符串.
	 * 
	 * 
	 * @param message
	 * @param params
	 * @return
	 */
	public static String formatMessage(String message, Object[] params) {
		if ((message == null) || (params == null) || (params.length == 0)) {
			return message;
		}

		return MessageFormat.format(message, params);
	}

}
