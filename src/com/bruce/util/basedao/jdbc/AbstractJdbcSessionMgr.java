package com.bruce.util.basedao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.bruce.util.basedao.AbstractSessionMgr;
import com.bruce.util.basedao.SessionMgr;
import com.bruce.util.basedao.TransIsoLevel;

/**
 * 
 * JDBC {@link SessionMgr} 抽象基类
 *
 */
public abstract class AbstractJdbcSessionMgr extends AbstractSessionMgr<Connection>
{
	/** 参考：{@link AbstractSessionMgr#loadDefalutTransIsoLevel()} */
	@Override
	protected void loadDefalutTransIsoLevel()
	{
		try
		{
			Connection session		= getSession();
			int level				= session.getTransactionIsolation();
			defaultTransIsoLevel	= TransIsoLevel.fromInt(level);
		}
		catch(SQLException e)
		{
			throw new JdbcException(e);
		}
		finally
		{
			closeSession();
		}
	}
	
	/** 参考：{@link SessionMgr#setSessionTransIsoLevel(TransIsoLevel)} */
	@Override
	public void setSessionTransIsoLevel(TransIsoLevel level)
	{
		try
		{
			Connection session = getSession();
			session.setTransactionIsolation(level.toInt());
		}
		catch(SQLException e)
		{
			throw new JdbcException(e);
		}
	}

	@Override
	public void beginTransaction()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.setAutoCommit(false);
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
		}
	}

	@Override
	public void commit()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.commit();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
			finally
			{
				try {session.setAutoCommit(true);} catch (SQLException ex) {}
			}
		}
	}

	@Override
	public void rollback()
	{
		Connection session = getSession();
		
		if(session != null)
		{
			try
			{
				session.rollback();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
			finally
			{
				try {session.setAutoCommit(true);} catch (SQLException ex) {}
			}
		}
	}

	@Override
	public void closeSession()
	{
		Connection session = localSession.get();
		localSession.set(null);

		if(session != null)
		{
			try
			{
				session.close();
			}
			catch(SQLException e)
			{
				throw new JdbcException(e);
			}
		}
	}
}
