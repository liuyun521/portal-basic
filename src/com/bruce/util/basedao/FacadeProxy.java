package com.bruce.util.basedao;

import java.lang.reflect.Method;
import java.util.Set;

import com.bruce.util.BeanHelper;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

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
		Class<?>[] argTypes		= mgr != null ? new Class<?>[] {mgr.getClass()}	: new Class<?>[] {};
		Object[] args			= mgr != null ? new Object[] {mgr}				: new Object[] {};
		Callback intercepter	= new Interceptor<F, M, S>(autoCommit, level);
		
		return getProxy(daoClass, argTypes, args, intercepter);
	}

	@SuppressWarnings("unchecked")
	private static final <F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> F getProxy(Class<F> daoClass, Class<?>[] argTypes, Object[] args, Callback intercepter)
	{
		Enhancer en = new Enhancer();
		
		en.setSuperclass(daoClass);
		en.setCallbackFilter(InterceptFilter.INSTANCE);
		Callback[] callbacks = {NoOp.INSTANCE, intercepter};
		en.setCallbacks(callbacks);
		en.setInterceptDuringConstruction(false);
		
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

	/** 事务回调拦截器 */
	private static class Interceptor<F extends AbstractFacade<M, S>, M extends SessionMgr<S>, S> implements MethodInterceptor
	{
		private boolean autoCommit;
		private TransIsoLevel transLevel;

		Interceptor(boolean autoCommit, TransIsoLevel transLevel)
		{
			this.autoCommit = autoCommit;
			this.transLevel = transLevel;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public final Object intercept(Object dao, Method method, Object[] args, MethodProxy proxy) throws Throwable
		{
			Object result	= null;
			F facade		= (F)dao;
			
			if(facade.isInvoking())
				result = proxy.invokeSuper(dao, args);
			else
			{
				facade.setInvoking(true);
				
				M manager				= facade.getManager();
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
	}
}
