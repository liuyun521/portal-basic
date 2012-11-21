package com.bruce.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Action Context 感知接口 */
public interface ActionContextAware
{
	/** 设置 {@link ServletContext} */
	void setServletContext(ServletContext servletContext);

	/** 设置 {@link HttpServletRequest} */
	void setRequest(HttpServletRequest request);

	/** 设置 {@link HttpServletResponse} */
	void setResponse(HttpServletResponse response);
}
