package com.bruce.ext.dao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bruce.util.basedao.AbstractFacade;
import com.bruce.util.basedao.FacadeProxy;
import com.bruce.util.basedao.SessionMgr;

/** 自动装配 DAO Bean 的注解类 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("rawtypes")
public @interface DaoBean
{
	/** 待注入的属性或成员变量的名称<br>
	 * （默认：注入 Action 定义中第一个 Dao 类型的属性或成员变量） */
	String value() default "";
	/** 待注入的 DAO 类型，如果该类不能实例化类则注入失败<br>
	 * （默认：待注入的属性或成员变量的类型） */
	Class<? extends AbstractFacade> daoClass() default AbstractFacade.class;
	/** app-config.xml 中定义的 {@link SessionMgr} 名称，
	 * 对应 {@link FacadeProxy#create(Class, SessionMgr)} 的第二个参数<br>
	 * （默认：DAO 的默认 {@link SessionMgr}，效果等同于调用
	 * {@link FacadeProxy#create(Class)}） */
	String mgrName() default "";
}
