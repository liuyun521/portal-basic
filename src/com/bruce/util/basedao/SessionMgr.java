package com.bruce.util.basedao;

/**
 * 
 * Session 管理器
 *
 * @param <S>	: 数据库连接类型
 */
public interface SessionMgr<S>
{
	/**
	 * 
	 * 初始化
	 * 
	 * @param args	: 自定义初始参数
	*/
	void initialize(String ... args);
	/** 注销 */
	void unInitialize();
	/** 开始事务 */
	void beginTransaction();
	/** 提交事务 */
	void commit();
	/** 回滚事务 */
	void rollback();
	/** 关闭数据库连接对象 */
	void closeSession();
	/** 获取当前线程相关的数据库连接对象，如果不存在则创建并返回新对象 */
	S getSession();
	/** 获取当前线程相关的数据库连接对象，如果不存在则返回 null */
	S currentSession();
	/** 获取当前配置文件 */
	String getConfigFile();
	/** 获取 {@link SessionMgr} 的默认事务隔离级别 */
	TransIsoLevel getDefalutTransIsoLevel();
	/** 设置当前线程相关的数据库连接对象的事务级别 */
	void setSessionTransIsoLevel(final TransIsoLevel level);
}
