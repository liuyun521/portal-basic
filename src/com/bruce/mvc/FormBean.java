package com.bruce.mvc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 自动装配 Form Bean 的注解类 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FormBean
{
	/** 
	 * 1、如果 {@link FormBean#value()} 为空（默认值）则把 {@link Action} 自身作为 Form Bean, 对其进行装配 <br>
	 * 2、如果 {@link FormBean#value()} 不为空则把名称为 {@link FormBean#value()} 的 {@link Action} 成员属性作为 Form Bean, 对其进行装配 
	 *  
	 *  */
	String value() default "";
}
