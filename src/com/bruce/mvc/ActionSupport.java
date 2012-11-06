package com.bruce.mvc;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bruce.tag.Message;
import com.bruce.util.http.HttpHelper;

/**
 * 
 * {@link Action} 对象公共基类。
 *
 */
public class ActionSupport implements Action, ActionContextAware, Validateable
{
	private ServletContext servletContext;
	private HttpServletRequest request;
	private HttpServletResponse response;

	/** 默认 {@link Action} 入口方法（返回 {@link Action#SUCCESS}） */
	public String execute() throws Exception
	{
		return SUCCESS;
	}
	
	/** {@link Validateable#validate()} 的默认实现 */
	@Override
	public boolean validate()
	{
		return true;
	}

	/** 获取 {@link HttpServletRequest} 的指定属性值 */
	public final <T> T getRequestAttribute(String name)
	{
		return HttpHelper.getRequestAttribute(request, name);
	}

	/** 设置 {@link HttpServletRequest} 的指定属性值 */
	public final <T> void setRequestAttribute(String name, T value)
	{
		HttpHelper.setRequestAttribute(request, name, value);
	}
	
	/** 删除 {@link HttpServletRequest} 的指定属性值 */
	public final void removeRequestAttribute(String name)
	{
		HttpHelper.removeRequestAttribute(request, name);
	}

	/** 获取 {@link HttpSession} 的指定属性值 */
	public final <T> T getSessionAttribute(String name)
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			return HttpHelper.getSessionAttribute(session, name);
		
		return null;
	}

	/** 设置 {@link HttpSession} 的指定属性值 */
	public final <T> void setSessionAttribute(String name, T value)
	{
		HttpSession session = getSession(true);
		HttpHelper.setSessionAttribute(session, name, value);
	}
	
	/** 删除 {@link HttpSession} 的指定属性值 */
	public final void removeSessionAttribute(String name)
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			HttpHelper.removeSessionAttribute(session, name);
	}
	
	/** 销毁 {@link HttpSession} */
	public final void invalidateSession()
	{
		HttpSession session = getSession(false);
		
		if(session != null)
			HttpHelper.invalidateSession(session);
	}

	/** 获取 {@link ServletContext} 的指定属性值 */
	public final <T> T getApplicationAttribute(String name)
	{
		return HttpHelper.getApplicationAttribute(servletContext, name);
	}

	/** 设置 {@link ServletContext} 的指定属性值 */
	public final <T> void setApplicationAttribute(String name, T value)
	{
		HttpHelper.setApplicationAttribute(servletContext, name, value);
	}
	
	/** 删除 {@link ServletContext} 的指定属性值 */
	public final void removeApplicationAttribute(String name)
	{
		HttpHelper.removeApplicationAttribute(servletContext, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，并去除前后空格 */
	public final String getParam(String name)
	{
		return HttpHelper.getParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值 */
	public final String getParamNoTrim(String name)
	{
		return HttpHelper.getParamNoTrim(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的参数名称集合 */
	public final List<String> getParamNames()
	{
		return HttpHelper.getParamNames(request);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值集合 */
	public final List<String> getParamValues(String name)
	{
		return HttpHelper.getParamValues(request, name);
	}
	
	/** 获取 {@link HttpServletRequest} 的所有参数名称和值 */
	public final Map<String, String[]> getParamMap()
	{
		return HttpHelper.getParamMap(request);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Boolean getBooleanParam(String name)
	{
		return HttpHelper.getBooleanParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final boolean getBooleanParam(String name, boolean def)
	{
		return HttpHelper.getBooleanParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Byte getByteParam(String name)
	{
		return HttpHelper.getByteParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final byte getByteParam(String name, byte def)
	{
		return HttpHelper.getByteParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Character getCharParam(String name)
	{
		return HttpHelper.getCharParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final char getCharParam(String name, char def)
	{
		return HttpHelper.getCharParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Short getShortParam(String name)
	{
		return HttpHelper.getShortParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final short getShortParam(String name, short def)
	{
		return HttpHelper.getShortParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Integer getIntParam(String name)
	{
		return HttpHelper.getIntParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final int getIntParam(String name, int def)
	{
		return HttpHelper.getIntParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Long getLongParam(String name)
	{
		return HttpHelper.getLongParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final long getLongParam(String name, long def)
	{
		return HttpHelper.getLongParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Float getFloatParam(String name)
	{
		return HttpHelper.getFloatParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final float getFloatParam(String name, float def)
	{
		return HttpHelper.getFloatParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Double getDoubleParam(String name)
	{
		return HttpHelper.getDoubleParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回默认值 */
	public final double getDoubleParam(String name, double def)
	{
		return HttpHelper.getDoubleParam(request, name, def);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Date getDateParam(String name)
	{
		return HttpHelper.getDateParam(request, name);
	}

	/** 获取 {@link HttpServletRequest} 的指定请求参数值，失败返回 null */
	public final Date getDateParam(String name, String format)
	{
		return HttpHelper.getDateParam(request, name, format);
	}

	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 属性或成员变量名完全一致) */
	public final <T> T createFormBean(Class<T> clazz)
	{
		return HttpHelper.createFormBean(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性或成员变量) */
	public final <T> T createFormBean(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBean(request, clazz, keyMap);
	}
	
	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 属性名完全一致) */
	public final <T> T createFormBeanByProperties(Class<T> clazz)
	{
		return HttpHelper.createFormBeanByProperties(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性) */
	public final <T> T createFormBeanByProperties(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBeanByProperties(request, clazz, keyMap);
	}
	
	/** 使用表单元素创建 Form Bean (表单元素的名称和 Form Bean 成员变量名完全一致) */
	public final <T> T createFormBeanByFieldValues(Class<T> clazz)
	{
		return HttpHelper.createFormBeanByFieldValues(request, clazz);
	}

	/** 使用表单元素创建 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 成员变量) */
	public final <T> T createFormBeanByFieldValues(Class<T> clazz, Map<String, String> keyMap)
	{
		return HttpHelper.createFormBeanByFieldValues(request, clazz, keyMap);
	}
	
	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 属性名完全一致) */
	public final <T> void fillFormBeanProperties(T bean)
	{
		HttpHelper.fillFormBeanProperties(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性) */
	public final <T> void fillFormBeanProperties(T bean, Map<String, String> keyMap)
	{
        HttpHelper.fillFormBeanProperties(request, bean, keyMap);
	}
	
	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 成员变量名完全一致) */
	public final <T> void fillFormBeanFieldValues(T bean)
	{
		HttpHelper.fillFormBeanFieldValues(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 成员变量) */
	public final <T> void fillFormBeanFieldValues(T bean, Map<String, String> keyMap)
	{
        HttpHelper.fillFormBeanFieldValues(request, bean, keyMap);
	}

	/** 使用表单元素填充 Form Bean (表单元素的名称和 Form Bean 属性或成员变量名完全一致) */
	public final <T> void fillFormBeanPropertiesOrFieldValues(T bean)
	{
		HttpHelper.fillFormBeanPropertiesOrFieldValues(request, bean);
	}
	
	/** 使用表单元素填充 Form Bean (用 keyMap 映射与表单元素名称不对应的 Form Bean 属性或成员变量) */
	public final <T> void fillFormBeanPropertiesOrFieldValues(T bean, Map<String, String> keyMap)
	{
		HttpHelper.fillFormBeanPropertiesOrFieldValues(request, bean, keyMap);
	}

	/** 获取所有 {@link Cookie} */
	public final Cookie[] getCookies()
	{
		return HttpHelper.getCookies(request);
	}
	
	/** 获取指定名称的 {@link Cookie} */
	public final Cookie getCookie(String name)
	{
		return HttpHelper.getCookie(request, name);
	}


	/** 获取指定名称的 {@link Cookie} 值，失败返回 null */
	public final String getCookieValue(String name)
	{
		return HttpHelper.getCookieValue(request, name);
	}

	/** 添加 {@link Cookie} */
	public final void addCookie(Cookie cookie)
	{
		HttpHelper.addCookie(response, cookie);
	}

	/** 添加 {@link Cookie} */
	public final void addCookie(String name, String value)
	{
		HttpHelper.addCookie(response, name, value);
	}

	/** 获取当前 {@link HttpSession} 的 {@link Locale} 属性 */
	public final Locale getLocale()
	{
		Locale locale = (Locale)getSessionAttribute(Message.SESSION_LOCALE_KEY);

		if(locale == null)
			locale = Locale.getDefault();

		return locale;
	}

	/** 设置当前 {@link HttpSession} 的 {@link Locale} 属性 */
	public final void setLocale(Locale locale)
	{
		setSessionAttribute(Message.SESSION_LOCALE_KEY, locale);
	}
	
	/** 获取 URL 地址在文件系统的绝对路径,
	 * 
	 * Servlet 2.4 以上通过 request.getServletContext().getRealPath() 获取,
	 * Servlet 2.4 以下通过 request.getRealPath() 获取。
	 *  
	 */
	public final String getRequestRealPath(String path)
	{
		return HttpHelper.getRequestRealPath(request, path);
	}
	
	/** 获取 URL 的  BASE 路径 */
	public final String getRequestBasePath()
	{
		return HttpHelper.getRequestBasePath(request);
	}

	/** 禁止浏览器缓存当前页面 */
	public final void setNoCacheHeader()
	{
		HttpHelper.setNoCacheHeader(response);
	}
	
	/** 获取 {@link ServletContext} */
	public final ServletContext getServletContext()
	{
		return servletContext;
	}

	/** 设置 {@link ServletContext} */
	@Override
	public void setServletContext(ServletContext servletContext)
	{
		this.servletContext = servletContext;
	}

	/** 获取 {@link HttpServletRequest} */
	public final HttpServletRequest getRequest()
	{
		return request;
	}

	/** 设置 {@link HttpServletRequest} */
	@Override
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	/** 获取 {@link HttpServletResponse} */
	public final HttpServletResponse getResponse()
	{
		return response;
	}

	/** 设置 {@link HttpServletResponse} */
	@Override
	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}

	/** 检查 {@link HttpSession} 对象是否存在 */
	public final boolean isSessionExist()
	{
		return getSession(false) != null;
	}

	/** 获取 {@link HttpSession} 对象，如果没有则进行创建。 */
	public final HttpSession getSession()
	{
		return getSession(true);
	}

	/** 获取 {@link HttpSession} 对象，如果没有则根据参数决定是否创建。 */
	public final HttpSession getSession(boolean create)
	{
		return HttpHelper.getSession(request, create);
	}

	/** 创建 {@link HttpSession} 对象，如果已存在则返回原对象。 */
	public final HttpSession createSession()
	{
		return getSession();
	}
}
