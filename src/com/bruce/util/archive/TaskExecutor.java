package com.bruce.util.archive;

import org.apache.tools.ant.Task;

/** 压缩 / 解压执行器抽象类 */
abstract public class TaskExecutor
{	
	protected String source;
	protected String target;
	protected String includes;
	protected String excludes;
	
	private Exception cause;
	
	/** 获取任务对象 */
	abstract protected Task getTask();
	
	/** 执行压缩或解压任务
	 * 
	 * @return	成功返回 true，失败返回 false，如果失败可通过 {@link TaskExecutor#getCause()} 获取失败原因
	 * 
	 */
	public boolean execute()
	{
		cause = null;
		
		try
		{
			Task task = getTask();
			task.execute();
		}
		catch(Exception e)
		{
			cause = e;
			return false;
		}
		
		return true;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param source	: 输入文件或文件夹。（输出文件或文件夹通过默认规则生成）
	 * */
	public TaskExecutor(String source)
	{
		this(source, null);
	}
	
	/**
	 * 构造函数
	 * 
	 * @param source	: 输入文件或文件夹
	 * @param target	: 输出文件或文件夹，如果 target 参数为 null，则通过默认规则生成输出文件或文件夹
	 * */
	public TaskExecutor(String source, String target)
	{
		this.source	= source;
		this.target	= target;
	}
	
	/** 获取输入文件或文件夹 */
	public String getSource()
	{
		return source;
	}

	/** 设置输入文件或文件夹 */
	public void setSource(String source)
	{
		this.source = source;
	}

	/** 获取输出文件或文件夹 */
	public String getTarget()
	{
		return target;
	}

	/** 设置输出文件或文件夹 */
	public void setTarget(String target)
	{
		this.target = target;
	}

	/** 获取包含文件的规则表达式 */
	public String getIncludes()
	{
		return includes;
	}

	/** 设置包含文件的规则表达式 */
	public void setIncludes(String includes)
	{
		this.includes = includes;
	}

	/** 获取排除文件的规则表达式 */
	public String getExcludes()
	{
		return excludes;
	}

	/** 设置排除文件的规则表达式 */
	public void setExcludes(String excludes)
	{
		this.excludes = excludes;
	}

	/** 获取压缩或解压任务的失败原因 */
	public Exception getCause()
	{
		return cause;
	}
}
