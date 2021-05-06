package org.oneboot.core.logging.sensitive;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 敏感数据字段
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
//@Retention(RetentionPolicy.RUNTIME)
//@Target({ ElementType.FIELD })
@Documented  //有关java doc的注解
@Retention(RetentionPolicy.RUNTIME)  
@Target({ ElementType.FIELD,ElementType.METHOD }) //针对方法
//@JacksonAnnotationsInside
//@JsonSerialize(using = JacksonDesensitize.class)
@Inherited
public @interface Sensitive {

	/**
	 * 敏感数据屏蔽方式
	 * 
	 * @return
	 */
	SensitiveTypeEnum type() default SensitiveTypeEnum.ALL;

	/**
	 * 附加条件值
	 * 
	 * @return
	 */
	String[] addition() default {};
}
