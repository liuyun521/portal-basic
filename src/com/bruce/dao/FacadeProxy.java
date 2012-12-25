/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Author	: Bruce Liang
 * Bolg		: http://www.cnblogs.com/ldcsaa
 * WeiBo	: http://weibo.com/u/1402935851
 * QQ Group	: http://qun.qq.com/#jointhegroup/gid/75375912
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bruce.dao;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import com.bruce.util.BeanHelper;
import com.bruce.util.CoupleKey;
import com.bruce.util.GeneralHelper;

/**
 * 
 * Facade 代理类
 *
 */
public class FacadeProxy
{
	/**
	 * 
	 * 获取 daoClass 的自动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 daoClass 的默认构造函数提供。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getAutoCommitProxy(Class<F> daoClass)
	{
		return getProxy(daoClass, true);
	}
	
	/**
	 * 
	 * 获取 daoClass 的自动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @param mgr		: {@link SessionMgr} 
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getAutoCommitProxy(Class<F> daoClass, M mgr)
	{
		return getProxy(daoClass, mgr, true);
	}
	
	/**
	 * 
	 * 获取 daoClass 的手动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 daoClass 的默认构造函数提供。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getManualCommitProxy(Class<F> daoClass)
	{
		return getProxy(daoClass, false);
	}
	
	/**
	 * 
	 * 获取 daoClass 的手动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @param mgr		: {@link SessionMgr} 
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getManualCommitProxy(Class<F> daoClass, M mgr)
	{
		return getProxy(daoClass, mgr, false);
	}

	/**
	 * 
	 * 获取 daoClass 的手动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @param level		: {@link TransIsoLevel}
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getManualCommitProxy(Class<F> daoClass, TransIsoLevel level)
	{
		return getProxy(daoClass, null, false, level);
	}

	/**
	 * 
	 * 获取 daoClass 的手动提交事务代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定。
	 * 
	 * @param daoClass	: 被代理的数据库访问类对象
	 * @param mgr		: {@link SessionMgr} 
	 * @param level		: {@link TransIsoLevel}
	 * @return			     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getManualCommitProxy(Class<F> daoClass, M mgr, TransIsoLevel level)
	{
		return getProxy(daoClass, mgr, false, level);
	}

	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 由 daoClass 的默认构造函数提供，是否自动提交事务 transactional 指定。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @param autoCommit	: 是否自动提交事务（为了提高执行效率，对于只执行查询的访问通常采用自动提交事务）
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getProxy(Class<F> daoClass, boolean autoCommit)
	{
		return getProxy(daoClass, null, autoCommit);
	}
	
	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定，是否自动提交事务由 autoCommit 参数指定。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @param mgr			: {@link SessionMgr} 
	 * @param autoCommit	: 是否自动提交事务（为了提高执行效率，对于只执行查询的访问通常采用自动提交事务）
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getProxy(Class<F> daoClass, M mgr, boolean autoCommit)
	{
		return getProxy(daoClass, mgr, autoCommit, TransIsoLevel.DEFAULT);
	}
	
	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定，是否自动提交事务由 autoCommit 参数指定。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @param mgr			: {@link SessionMgr} 
	 * @param autoCommit	: 是否自动提交事务（为了提高执行效率，对于只执行查询的访问通常采用自动提交事务）
	 * @param level			: {@link TransIsoLevel}
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getProxy(Class<F> daoClass, M mgr, boolean autoCommit, TransIsoLevel level)
	{
		Callback intercepter = new ProxyInterceptor(autoCommit, level);
		return getProxy(daoClass, mgr, intercepter);
	}

	@SuppressWarnings("unchecked")
	private static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getProxy(Class<F> daoClass, M mgr, Callback intercepter)
	{
		Enhancer en = new Enhancer();
		
		en.setSuperclass(daoClass);
		en.setCallbackFilter(InterceptFilter.INSTANCE);
		Callback[] callbacks = {NoOp.INSTANCE, intercepter};
		en.setCallbacks(callbacks);
		en.setInterceptDuringConstruction(false);
		
		Class<?>[] argTypes	= mgr != null ? new Class<?>[] {mgr.getClass()}	: new Class<?>[] {};
		Object[] args		= mgr != null ? new Object[] {mgr}				: new Object[] {};

		return (F)en.create(argTypes, args);
	}
	
	/** 函数回调过滤器 */
	private static class InterceptFilter implements CallbackFilter
	{
		private static final InterceptFilter INSTANCE	= new InterceptFilter();
		private static final Set<Method> FILTER_METHODS	= BeanHelper.getAllMethods(AbstractFacade.class);
		
		@Override
		public final int accept(Method method)
		{
			if(FILTER_METHODS.contains(method))
				return 0;
			
			return 1;
		}
	}
	
	@SuppressWarnings("rawtypes")
	private static final Object intercept(Object dao, Method method, Object[] args, MethodProxy proxy, boolean autoCommit, TransIsoLevel transLevel) throws Throwable
	{
		Object result			= null;
		AbstractFacade facade	= (AbstractFacade)dao;
		
		if(facade.isInvoking())
			result = proxy.invokeSuper(dao, args);
		else
		{
			facade.setInvoking(true);
			
			SessionMgr manager		= facade.getManager();
			TransIsoLevel defLevel	= manager.getDefalutTransIsoLevel();
			boolean alterTransLevel	= (!autoCommit && transLevel != TransIsoLevel.DEFAULT && transLevel != defLevel);
			
        	try
        	{
        		if(alterTransLevel)	manager.setSessionTransIsoLevel(transLevel);
        		if(!autoCommit)		manager.beginTransaction();
        		result = proxy.invokeSuper(dao, args);
        		if(!autoCommit)		manager.commit();
        	}
        	catch(Exception e)
        	{
        		if(!autoCommit) {try{manager.rollback();} catch (Exception ex) {}}
        		throw new DAOException(e);
        	}
        	finally
        	{
        		if(alterTransLevel) try {manager.setSessionTransIsoLevel(defLevel);} catch (Exception ex) {}
        		try {manager.closeSession();} catch (Exception ex) {}
        		
        		facade.setInvoking(false);
        	}
		}

		return result;
	}

	/** 事务回调拦截器 */
	private static class ProxyInterceptor implements MethodInterceptor
	{
		private TransAttr transAttr;

		private ProxyInterceptor(boolean autoCommit, TransIsoLevel transLevel)
		{
			this.transAttr = new TransAttr(autoCommit, transLevel);
		}
		
		@Override
		public final Object intercept(Object dao, Method method, Object[] args, MethodProxy proxy) throws Throwable
		{
			if(!Modifier.isPublic(method.getModifiers()))
				return proxy.invokeSuper(dao, args);

			return FacadeProxy.intercept(dao, method, args, proxy, transAttr.autoCommit, transAttr.transLevel);
		}
	}
	
	private static class TransAttr
	{
		private static final TransAttr DEFAULT = new TransAttr();
		
		private boolean autoCommit;
		private TransIsoLevel transLevel;
		
		private TransAttr()
		{
			this(false, TransIsoLevel.DEFAULT);
		}
		
		private TransAttr(boolean autoCommit, TransIsoLevel transLevel)
		{
			this.autoCommit = autoCommit;
			this.transLevel = transLevel;
		}
	}
	
	/* **************************************************************************************************** */
	//											新式 DAO 操作方法												//
	/* **************************************************************************************************** */
	
	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 由 daoClass 的默认构造函数提供。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F create(Class<F> daoClass)
	{
		return create(daoClass, (M)null);
	}
	
	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 的名称由 mgr 指定。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @param mgrName		: app-config.xml 中 {@link SessionMgr} 的名称 
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */

	/**
	 * 
	 * 获取 daoClass 的代理对象。
	 * 其中，{@link SessionMgr} 由 mgr 指定。
	 * 
	 * @param daoClass		: 被代理的数据库访问类对象
	 * @param mgr			: {@link SessionMgr} 
	 * @return				     与 daoClass 有相同接口的代理对象
	 * 
	 */
	public static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F create(Class<F> daoClass, M mgr)
	{
		return getProxy(daoClass, mgr, ProxyInterceptor2.INSTANCE);
	}

	/** 事务回调拦截器 */
	private static class ProxyInterceptor2 implements MethodInterceptor
	{
		private static final ProxyInterceptor2 INSTANCE = new ProxyInterceptor2();
		private static final Map<CoupleKey<Class<?>, Method>, TransAttr> TRANS_ATTR_MAP = new HashMap<CoupleKey<Class<?>, Method>, TransAttr>();
		
		@Override
		public final Object intercept(Object dao, Method method, Object[] args, MethodProxy proxy) throws Throwable
		{
			if(!Modifier.isPublic(method.getModifiers()))
				return proxy.invokeSuper(dao, args);
			
			Class<?> superClass = dao.getClass().getSuperclass();
			CoupleKey<Class<?>, Method> key = new CoupleKey<Class<?>, Method>(superClass, method);
			
			checkTransAttrMap(dao, key);
			
			TransAttr transAttr = TRANS_ATTR_MAP.get(key);
			return FacadeProxy.intercept(dao, method, args, proxy, transAttr.autoCommit, transAttr.transLevel);
		}

		private static final void checkTransAttrMap(Object dao, CoupleKey<Class<?>, Method> key)
		{
			if(!TRANS_ATTR_MAP.containsKey(key))
			{
				TransAttr transAttr	= null;
				Transaction trans	= key.getKey2().getAnnotation(Transaction.class);
				
				if(trans == null)
					trans = key.getKey1().getAnnotation(Transaction.class);
				if(trans == null)
					transAttr = TransAttr.DEFAULT;
				else
					transAttr = new TransAttr(!trans.value(), trans.level());
				
				GeneralHelper.syncTryPut(TRANS_ATTR_MAP, key, transAttr);
			}
		}
	}

}
