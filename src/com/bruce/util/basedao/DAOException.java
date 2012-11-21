package com.bruce.util.basedao;


/**
 * 
 * 数据库访问相关异常（运行期异常）
 * 
 */
@SuppressWarnings("serial")
public class DAOException extends RuntimeException
{
	public DAOException()
	{
		
	}
	
	public DAOException(String desc)
	{
		super(desc);
	}
	
	public DAOException(Throwable e)
	{
		super(e);
	}
	
	public DAOException(String desc, Throwable e)
	{
		super(desc, e);
	}
}
