/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Author	: Bruce Liang
 * Bolg		: http://www.cnblogs.com/ldcsaa
 * WeiBo	: http://weibo.com/u/1402935851
 * QQ Group	: http://qun.qq.com/#jointhegroup/gid/75375912
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bruce.ext.rest;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.bruce.ext.rest.renderer.RenderType;
import com.bruce.ext.rest.renderer.Renderer;
import com.bruce.mvc.Action;
import com.bruce.mvc.ActionSupport;
import com.bruce.util.BeanHelper;
import com.bruce.util.GeneralHelper;
import com.bruce.util.Result;
import com.bruce.util.http.HttpHelper;

/** 处理 REST 请求的 {@link Action} 基类<br>
 * 继承 {@link ActionSupport} 并实现 {@link Rest} 接口
 */
public abstract class RestActionSupport extends ActionSupport implements Rest
{
	private static final String PLACEHOLDER_PATTERN				= "([^/,;]+)";
	private static final char PLACEHOLDER_BEGING_CHAR			= '{';
	private static final char PLACEHOLDER_END_CHAR				= '}';
	
	private static final String RAILS_REST_INDEX_PATTERN		= "";
	private static final String RAILS_REST_CREATE_PATTERN		= "";
	private static final String RAILS_REST_DELETEALL_PATTERN	= "";
	private static final String RAILS_REST_UPDATE_PATTERN		= "/{0}";
	private static final String RAILS_REST_DELETE_PATTERN		= "/{0}";
	private static final String RAILS_REST_SHOW_PATTERN			= "/{0}";
	private static final String RAILS_REST_EDIT_PATTERN			= "/{0}/edit";
	private static final String RAILS_REST_EDITNEW_PATTERN		= "/new";
	
	private static final RestScheme RAILS_REST_INDEX_SCHEME		= new RestScheme(RequestType.GET,	 RAILS_REST_INDEX_PATTERN);
	private static final RestScheme RAILS_REST_CREATE_SCHEME	= new RestScheme(RequestType.POST,	 RAILS_REST_CREATE_PATTERN);
	private static final RestScheme RAILS_REST_DELETEALL_SCHEME	= new RestScheme(RequestType.DELETE, RAILS_REST_DELETEALL_PATTERN);
	private static final RestScheme RAILS_REST_UPDATE_SCHEME	= new RestScheme(RequestType.PUT,	 RAILS_REST_UPDATE_PATTERN);
	private static final RestScheme RAILS_REST_DELETE_SCHEME	= new RestScheme(RequestType.DELETE, RAILS_REST_DELETE_PATTERN);
	private static final RestScheme RAILS_REST_SHOW_SCHEME		= new RestScheme(RequestType.GET,	 RAILS_REST_SHOW_PATTERN);
	private static final RestScheme RAILS_REST_EDIT_SCHEME		= new RestScheme(RequestType.GET,	 RAILS_REST_EDIT_PATTERN);
	private static final RestScheme RAILS_REST_EDITNEW_SCHEME	= new RestScheme(RequestType.GET,	 RAILS_REST_EDITNEW_PATTERN);
	
	private static final Map<Class<? extends RestActionSupport>, List<RestInfo>> REST_INFO_MAP
						 = new HashMap<Class<? extends RestActionSupport>, List<RestInfo>>();
	
	private static final Class<? extends Annotation>[] REQUEST_TYPES = RequestType.toAnnotationClasses();

	private RestContext restContext;
	
	/** REST 请求的默认入口方法，它内部会调用 {@link RestActionSupport#process()} 处理 REST 请求<br>
	 * （注：一般情况下不建议子类改写本方法） */
	@Override
	public String execute() throws Exception
	{
		return process();
	}

	/** 实现 REST 请求整体处理逻辑，它内部会调用相应的 REST 请求处理方法<br>
	 * （注：一般情况下不建议子类改写本方法） */
	protected String process()
	{
		restContext = getRequestAttribute(Rest.Constant.REQ_ATTR_REST_CONTEXT);
		
		if(restContext == null)
			throw new RestException("invalid REST Request: Rest Context not found");
		
		checkPatternMap();
		
		RestResult rs = processRestRequest();
		
		if(rs == null)
			return processNotMatch();
		
		return processRestResult(rs);
	}

	private void checkPatternMap()
	{
		Class<? extends RestActionSupport> thisClass = this.getClass();
		
		if(!REST_INFO_MAP.containsKey(thisClass))
		{
			List<RestInfo> infos = parseRestInfos(thisClass);
			GeneralHelper.syncTryPut(REST_INFO_MAP, thisClass, infos);
		}
	}
	
	private static List<RestInfo> parseRestInfos(Class<? extends RestActionSupport> clazz)
	{
		List<RestInfo> infos	= new ArrayList<RestInfo>();
		Set<RestScheme> schemes	= new HashSet<RestScheme>();
		
		checkAnnotationRestInfos(clazz, infos, schemes);
		checkDefaultRestInfos(clazz, infos, schemes);
		sortRestInfos(infos);
		
		return infos.isEmpty() ? null : infos;
	}

	private static void checkAnnotationRestInfos(Class<? extends RestActionSupport> clazz, List<RestInfo> infos, Set<RestScheme> schemes)
	{
		Method[] methods = clazz.getMethods();
		
		for(Method method : methods)
		{
			if(!RestResult.class.isAssignableFrom(method.getReturnType()) || !BeanHelper.isPublicInstanceMethod(method))
				continue;
			
			for(Class<? extends Annotation> reqType : REQUEST_TYPES)
			{
				Annotation annotation = method.getAnnotation(reqType);
				
				if(annotation != null)
				{
					String[] values = RequestType.getAnnotationValue(annotation);
					
					for(String value : values)
					{
						RestScheme scheme = new RestScheme(annotation, value);
						if(!schemes.contains(scheme))
						{
							RestInfo info = parseRestInfo(method, scheme);
							
							infos.add(info);
							schemes.add(scheme);	
						}
					}
				}
			}
		}
	}

	private static RestInfo parseRestInfo(Method method, RestScheme scheme)
	{
		RestInfo info	 = new RestInfo();
		info.requestType = scheme.requestType;
		info.method		 = method;
		
		String format	 = GeneralHelper.escapeRegexChars(scheme.format, PLACEHOLDER_BEGING_CHAR, PLACEHOLDER_END_CHAR);
		int length		 = format.length();
		
		boolean isOK = true;
		int begin = -1, end  = -1;
		StringBuilder sb	 = new StringBuilder();
		List<Integer> orders = new ArrayList<Integer>();

		do
		{
			begin = format.indexOf(PLACEHOLDER_BEGING_CHAR, end + 1);
			
			if(begin != -1)
			{
				sb.append(format.substring(end + 1, begin));
				end = format.indexOf(PLACEHOLDER_END_CHAR, begin + 1);
				
				if(end == -1) isOK = false;
				
				if(isOK)
				{
					int order = GeneralHelper.str2Int(format.substring(begin + 1, end), -1);
					if(order < 0) isOK = false;
					
					if(isOK)
					{
						orders.add(order);
						sb.append(PLACEHOLDER_PATTERN);
					}
				}
			}
		} while(isOK && begin != -1);
		
		if(isOK)
		{
			if(end < length - 1)
				sb.append(format.substring(end + 1));
			
			info.pattern = Pattern.compile(sb.toString());
			int size	 = orders.size();
			
			info.orders = new int[size];
			for(int i = 0; i < size; i++)
				info.orders[i] = orders.get(i);
		}
		else
		{
			String msg = String.format("parse REST Scheme fail: invalid REST Scheme '%s' format in '%s'", scheme.format, method);
			throw new RestException(msg);
		}
		
		return info;
	}
	
	private static void checkDefaultRestInfos(Class<? extends RestActionSupport> clazz, List<RestInfo> infos, Set<RestScheme> schemes)
	{
		try
		{
			Method m = clazz.getMethod(Rest.INDEX);
			if(!schemes.contains(RAILS_REST_INDEX_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_INDEX_SCHEME));
			
			m = clazz.getMethod(Rest.CREATE);
			if(!schemes.contains(RAILS_REST_CREATE_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_CREATE_SCHEME));

			m = clazz.getMethod(Rest.DELETE_ALL);
			if(!schemes.contains(RAILS_REST_DELETEALL_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_DELETEALL_SCHEME));
			
			m = clazz.getMethod(Rest.UPDATE, int.class);
			if(!schemes.contains(RAILS_REST_UPDATE_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_UPDATE_SCHEME));
			
			m = clazz.getMethod(Rest.DELETE, int.class);
			if(!schemes.contains(RAILS_REST_DELETE_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_DELETE_SCHEME));
			
			m = clazz.getMethod(Rest.SHOW, int.class);
			if(!schemes.contains(RAILS_REST_SHOW_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_SHOW_SCHEME));
			
			m = clazz.getMethod(Rest.EDIT, int.class);
			if(!schemes.contains(RAILS_REST_EDIT_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_EDIT_SCHEME));
			
			m = clazz.getMethod(Rest.EDIT_NEW);
			if(!schemes.contains(RAILS_REST_EDITNEW_SCHEME) && m.getDeclaringClass() != RestActionSupport.class)
				infos.add(parseRestInfo(m, RAILS_REST_EDITNEW_SCHEME));
		}
		catch(Exception e)
		{
			throw new RestException(e);
		}
	}

	private static void sortRestInfos(List<RestInfo> infos)
	{
		Collections.sort(infos, new Comparator<RestInfo>()
		{
			@Override
			public int compare(RestInfo ri1, RestInfo ri2)
			{
				int minus = ri1.orders.length - ri2.orders.length;
				
				if(minus == 0)
					minus = ri2.pattern.pattern().length() - ri1.pattern.pattern().length();
				
				return minus;
			}
		});
	}

	private RestResult processRestRequest()
	{
		Class<? extends RestActionSupport> clazz = this.getClass();
		List<RestInfo> infos = REST_INFO_MAP.get(clazz);
		
		if(infos != null)
		{
			for(RestInfo info : infos)
			{
				if(restContext.requestType == info.requestType)
				{
					Matcher m = info.pattern.matcher(restContext.requestPath);
					if(m.matches()) return invokeRestMethod(info, m);
				}
			}
		}
		
		return null;
	}
	
	private RestResult invokeRestMethod(RestInfo info, Matcher m)
	{
		Class<?>[] types = info.method.getParameterTypes();
		Object[] args	 = new Object[types.length];
		
		if(types.length > 0)
		{
			int count = m.groupCount();
			
			for(int i = 0; i < types.length; i ++)
			{
				String value = null;
				for(int j = 0; j < info.orders.length; j++)
				{
					if(i == info.orders[j])
					{
						if(j < count)
							value = m.group(j + 1);
						
						break;
					}
				}
				
				Type genericType = null;
				if(Collection.class.isAssignableFrom(types[i]))
					genericType = info.method.getGenericParameterTypes()[i];				
				
				Result<Boolean, Object> result = BeanHelper.parseValue(value, types[i], genericType);
				
				if(result.getFlag())
					args[i] = result.getValue();
			}
		}
		
		RestResult result = BeanHelper.invokeMethod(this, info.method, args);
		
		if(result == null)
		{
			String msg = String.format("the return value of '%s' must not be null", info.method);
			throw new RestException(msg);
		}
		
		return result;
	}

	/** 处理 REST 请求格式与任何 REST 入口方法都不匹配的情形。 <br>
	 * 默认：向客户端报告 404 错误并返回 {@link Action#NONE}。 <br>
	 * 子类可以改写这个方法实现自己的处理行为。 */
	protected String processNotMatch()
	{
		try
		{
			String msg = String.format("found none REST Pattern match {%s, '%s'} in '%s'",
						 restContext.requestType, restContext.requestPath, this.getClass().getName());
			getResponse().sendError(HttpServletResponse.SC_NOT_FOUND, msg);
		}
		catch(IOException e)
		{
			throw new RestException(e);
		}
		
		return NONE;
	}
	
	private String processRestResult(RestResult rrs)
	{
    	Renderer renderer = getRenderer(rrs);
  
		try
		{
	    	prepareRender(renderer, rrs);
    		return renderer.render(getRequest(), getResponse(), rrs);
		}
		catch(Exception e)
		{
			throw new RestException(e);
		}
	}

	/** 获取输出模型的 {@link Renderer}。 <br>
	 * 默认：返回当前 {@link RenderType} 的默认 {@link Renderer}。 <br> 
	 * 子类可以改写这个方法返回自定义 {@link Renderer}。 */
	protected Renderer getRenderer(RestResult rrs)
	{
		return restContext.renderType.getDefaultRenenderer();
	}

	/** 这个方法在执行 {@link Renderer#render(HttpServletRequest, HttpServletResponse, RestResult)} 前被调用，
	 * 用于对输出结果进行额外处理（如：设置一些 HTTP 响应头）。 <br>
	 * 默认：根据 {@link Renderer} 类型设置 Content-Type HTTP 响应头。 <br>
	 * 子类可以改写这个方法实现自己的处理行为。 */
	protected void prepareRender(Renderer renderer, RestResult rrs)
	{
		HttpServletResponse response = getResponse();
		
		String contentType 	= renderer.getContentType();
    	if(GeneralHelper.isStrNotEmpty(contentType))
    	{
    		String encoding = response.getCharacterEncoding();
    		if(encoding == null) encoding = GeneralHelper.DEFAULT_ENCODING;
    		HttpHelper.setContentType(response, contentType, encoding);
    	}
	}

	@Override
	public RestResult index() throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult create() throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult deleteAll() throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult update(int id) throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult delete(int id) throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult show(int id) throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult edit(int id) throws Exception
	{
		return throwNotImplementException();
	}

	@Override
	public RestResult editNew() throws Exception
	{
		return throwNotImplementException();
	}

	private RestResult throwNotImplementException()
	{
		throw new RestException(String.format("not implement, '%s' should override this method", this.getClass().getName()));
	}

	public RestContext getRestContext()
	{
		return restContext;
	}
	
	private static class RestInfo
	{
		RequestType requestType;
		Pattern pattern;
		Method method;
		int[] orders;
	}
	
	private static class RestScheme
	{
		RequestType requestType;
		String format;
		
		RestScheme(Annotation annotation, String format)
		{
			this(RequestType.fromAnnotation(annotation), format);
		}		
		
		RestScheme(RequestType requestType, String format)
		{
			this.requestType = requestType;
			this.format = format;
		}

		@Override
		public int hashCode()
		{
			return requestType.hashCode() ^ format.hashCode();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof RestScheme)
			{
				RestScheme other = (RestScheme)obj;
				return requestType.equals(other.requestType) && format.equals(other.format);
			}
			
			return false;
		}
	}
}
