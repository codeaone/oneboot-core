package org.oneboot.core.logging.logback;

import java.util.stream.Stream;

import org.oneboot.core.logging.filter.FastjsonSensitiveFilter;
import org.slf4j.helpers.MessageFormatter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class SensitiveConverter extends MessageConverter {

	@Override
	public String convert(ILoggingEvent event) {
		try {
			return MessageFormatter
					.arrayFormat(event.getMessage(), Stream.of(event.getArgumentArray()).map(argument -> {
						Class<? extends Object> aClass = argument.getClass();
						if (aClass.equals(String.class)) {
							// 如果是字符串则直接输出
							return argument;
						}
						if (aClass.equals(JSONObject.class)) {
							// 若是JSONObject则输出其
							return argument.toString();
						}
						return JSON.toJSONString(argument,new FastjsonSensitiveFilter());
//						return argument.toString();
					}).toArray()).getMessage();
		} catch (Exception e) {
			return event.getMessage();
		}
	}

}
