package org.oneboot.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RPC接口调用配置
 * 
 * @author shiqiao.pro
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcInvok {
	/**
	 * 方法名称
	 */
	public String name();

	/**
	 * 场景
	 */
	public String scene() default "";

	/**
	 * 是否为网关调用
	 * 
	 * @return
	 */
	public boolean openapi() default false;
}
