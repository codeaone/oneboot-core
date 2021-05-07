package org.oneboot.core.logging.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.oneboot.core.logging.sensitive.Sensitive;
import org.oneboot.core.logging.sensitive.SensitiveType;
import org.oneboot.core.logging.sensitive.SensitiveTypeFactory;

import com.alibaba.fastjson.serializer.ValueFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * JSONObject.toJSONString(body,new FastjsonDesensitizeFilter()) 这个应该是自动注册才行
 * 
 * @author shiqiao.pro
 * 
 */
@Slf4j
public class FastjsonSensitiveFilter implements ValueFilter {

	@Override
	public Object process(Object object, String name, Object value) {
		// System.out.println(object);
		if (null == value || !(value instanceof String) || ((String) value).length() == 0) {
			return value;
		}
		// 获取object类的字段属性
		try {
			Field field = object.getClass().getDeclaredField(name);

			// 字段上面可能会有多个注解，会有问题吗 ？ TODO
			Annotation[] list = field.getAnnotations();
			for (Annotation annotation : list) {
				System.out.println(annotation);
				System.out.println(annotation.annotationType());
				System.out.println(annotation.getClass().getName());
				if (annotation instanceof Sensitive) {
					System.out.println("=============");
				}
			}
			System.out.println();
			Sensitive sensitive = field.getAnnotation(Sensitive.class);
			if (null == sensitive) {
				return value;
			}

			SensitiveType sensitiveType = SensitiveTypeFactory.getSensitiveType(sensitive.type().getType());
			if (sensitiveType == null) {
				return value;
			}
			return sensitiveType.shield(value, sensitive.addition());
		} catch (NoSuchFieldException | SecurityException e) {
			log.error("JOSN ValueFilter process error", e);
		}

		return value;
	}

}
