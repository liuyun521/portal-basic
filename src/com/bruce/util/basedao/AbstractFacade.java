package com.bruce.util.basedao;

/**
 * 
 * Facade 抽象基类
 *
 * @param <M>	: {@link SessionMgr}
 * @param <S>	: 数据库连接类型
 */
public abstract class AbstractFacade<M extends SessionMgr<S>, S>
{
	private M manager;
	private ThreadLocal<Boolean> invoking = new ThreadLocal<Boolean>();
	
	protected AbstractFacade(M mgr)
	{
		manager = mgr;
	}
	
	/** 获取关联的 {@link SessionMgr} */
	public M getManager()
	{
		return manager;
	}
	
	/** 获取数据库连接 */
	protected S getSession()
	{
		return manager.getSession();
	}
		
	boolean isInvoking()
	{
		return Boolean.TRUE.equals(invoking.get());
	}
	
	void setInvoking(boolean value)
	{
		invoking.set(value);
	}
}
