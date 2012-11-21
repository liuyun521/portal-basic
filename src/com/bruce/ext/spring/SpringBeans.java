package com.bruce.ext.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 自动装配多个 Spring Bean 的注解类 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpringBeans
{
	/** {@link SpringBean} 数组<br>
	 * （默认：空数组，不注入任何 Spring Bean）*/
	SpringBean[] value() default {};
}
