package com.bruce.ext.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 自动装配多个 DAO Bean 的注解类 */
@Inherited
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DaoBeans
{
	/** {@link DaoBean} 数组<br> 
	 * （默认：注入 Action 定义中所有 Dao 类型的属性或成员变量）*/
	DaoBean[] value() default {@DaoBean};
}
