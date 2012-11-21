package com.bruce.util.basedao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** DAO 方法的事务属性注解类 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Transaction
{
	/** 是否强制启动事务 */
	boolean value() default true;
	/** 事务级别（参考: {@link TransIsoLevel}） */
	TransIsoLevel level() default TransIsoLevel.DEFAULT;
}
