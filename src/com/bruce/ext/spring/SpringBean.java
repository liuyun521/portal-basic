package com.bruce.ext.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 自动装配 Spring Bean 的注解类<br> 
 * 
 * 1、同时指定 {@link SpringBean#name()} 和 {@link SpringBean#type()}：按 Bean 名称和类型注入<br>
 * 2、只指定 {@link SpringBean#name()}：按 Bean 名称注入<br>
 * 3、只指定 {@link SpringBean#type()}：按 Bean 类型注入<br>
 * 4、{@link SpringBean#name()} 和 {@link SpringBean#type()} 都不指定：按 Bean 名称注入，其中 Bean 名称与 {@link SpringBean#value()} 一致
 * 
 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringBean
{
	/** 待注入的属性或成员变量的名称 */
	String value();
	/** Bean 类型 */
	Class<?> type() default Object.class;
	/** Bean 名称 */
	String name() default "";
}
