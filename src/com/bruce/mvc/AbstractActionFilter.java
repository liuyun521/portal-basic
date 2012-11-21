package com.bruce.mvc;

/** {@link ActionFilter} 抽象基类，实现空的 init() 和 destroy() 方法 */
abstract public class AbstractActionFilter implements ActionFilter
{

	/** 空方法 */
	@Override
	public void init()
	{

	}

	/** 空方法 */
	@Override
	public void destroy()
	{

	}
}
