package org.oneboot.core.logging.sensitive;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SensitiveTypeFactory {

	public static final Map<String, SensitiveType> TYPE_CACHE = new HashMap<>(16);

	static {
		try {
			for (SensitiveTypeEnum type : SensitiveTypeEnum.values()) {
				TYPE_CACHE.put(type.getType(), type.getImplementClass().newInstance());
			}
		} catch (Exception e) {
			log.error("init newInstance SensitiveType error", e);
		}
	}

	public static SensitiveType getSensitiveType(String type) {
		return TYPE_CACHE.get(type);
	}
}
