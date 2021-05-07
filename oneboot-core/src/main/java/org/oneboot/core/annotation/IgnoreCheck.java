package org.oneboot.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 目前所有的接口都是需要进行会话检查的，打上这个注解就可以跳过会话检查
 * 
 * @author shiqiao.pro
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreCheck {

}
