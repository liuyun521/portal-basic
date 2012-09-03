package com.bruce.mvc;

import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Action 执行器，在 {@link ActionFilter} 的 doFilter() 方法中使用 */
public class ActionExecutor
{
	private Queue<ActionFilter> filters;
	private Action action;
	private ServletContext context;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@SuppressWarnings("unchecked")
	ActionExecutor(LinkedList<ActionFilter> filters, Action action, ServletContext context, HttpServletRequest request, HttpServletResponse response)
	{
		this.action		= action;
		this.context	= context;
		this.request	= request;
		this.response	= response;
		this.filters	= (Queue<ActionFilter>)filters.clone();
	}
	
	/** 
	 * 调用 {@link ActionFilter} 拦截器堆栈中的下一个拦截器的 doFilter() 方法或
	 * 调用 {@link Action} 的 execute() 方法 
	*/
	public String invoke() throws Exception
	{
		if(filters.isEmpty())
			return execute(action);
		else
		{
			ActionFilter filter = filters.poll();
			return filter.doFilter(this);
		}
	}

	static final String execute(Action action) throws Exception
	{
		boolean valid = true;
		
		if(action instanceof Validateable)
			valid = ((Validateable)action).validate();
		
		return valid ? action.execute() : Action.INPUT;
	}
	
	/** 获取当前被拦截的 {@link Action} 对象 */
	public Action getAction()
	{
		return action;
	}

	/** 获取 {@link ServletContext} 对象 */
	public ServletContext getServletContext()
	{
		return context;
	}

	/** 获取 {@link HttpServletRequest} 对象 */
	public HttpServletRequest getRequest()
	{
		return request;
	}

	/** 获取 {@link HttpServletResponse} 对象 */
	public HttpServletResponse getResponse()
	{
		return response;
	}
}
