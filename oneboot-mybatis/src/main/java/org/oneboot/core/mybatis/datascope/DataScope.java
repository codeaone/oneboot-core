package org.oneboot.core.mybatis.datascope;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据权限过滤注解
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
	/**
	 * 条件字段名
	 */
	public String deptField() default "dept_id";

	public String userField() default "user_id";

}
