package com.bruce.util.basedao;

/**
 * 
 * SessionMgr 抽象基类，实现 {@link SessionMgr} 接口
 *
 * @param <S>	: 数据库连接类型
 */
public abstract class AbstractSessionMgr<S> implements SessionMgr<S>
{
	/** 配置文件 */
	protected String configFile;
	/** 默认事务隔离级别 */
	protected TransIsoLevel defaultTransIsoLevel;
	/** 线程局部存储对象，为每个线程提供不同的 Session 对象 */
	protected final ThreadLocal<S> localSession = new ThreadLocal<S>();
	
	/** 加载 {@link SessionMgr} 的默认事务隔离级别 */
	abstract protected void loadDefalutTransIsoLevel();
	
	/** 参考：{@link SessionMgr#getDefalutTransIsoLevel()} */
	@Override
	public TransIsoLevel getDefalutTransIsoLevel()
	{
		return defaultTransIsoLevel;
	}

	/** 参考：{@link SessionMgr#currentSession()} */
	@Override
	public S currentSession()
	{
		return localSession.get();
	}
	
	/** 参考：{@link SessionMgr#getConfigFile()} */
	@Override
	public String getConfigFile()
	{
		return configFile;
	}
}
