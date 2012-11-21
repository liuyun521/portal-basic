package com.bruce.util.basedao.jdbc;


/**
 * 
 * JDBC 异常类，继承 {@link RuntimeException}
 * 
 */
@SuppressWarnings("serial")
public class JdbcException extends RuntimeException
{
	public JdbcException()
	{
		
	}
	
	public JdbcException(String desc)
	{
		super(desc);
	}
	
	public JdbcException(Throwable e)
	{
		super(e);
	}
	
	public JdbcException(String desc, Throwable e)
	{
		super(desc, e);
	}
}
