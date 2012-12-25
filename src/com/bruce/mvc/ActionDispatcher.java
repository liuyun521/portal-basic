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

package com.bruce.mvc;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.bruce.mvc.Action.ResultType;
import com.bruce.util.BeanHelper;
import com.bruce.util.GeneralHelper;
import com.bruce.util.http.HttpHelper;

/**
 * 
 * MVC 前端控制器，实现为 {@link Filter}
 *
 */
public class ActionDispatcher implements Filter
{
	private static final String PATH_SEPARATOR					= HttpHelper.URL_PATH_SEPARATOR;
	private static final String CURRENT_PATH_PREFIX				= "./";
	private static final String GLOBAL_KEY						= "global";
	private static final String INCLUDE_KEY						= "include";
	private static final String INCLUDE_FILE_KEY				= "file";
	private static final String ACTIONS_KEY						= "actions";
	private static final String ACTIONS_PATH_KEY				= "path";
	private static final String ACTION_ENTRY_SEPARATOR			= "!";
	private static final String ACTION_KEY						= "action";
	private static final String ACTION_NAME_KEY					= "name";
	private static final String ACTION_CLASS_KEY				= "class";
	private static final String ACTION_ENTRY_KEY				= "entry";
	private static final String ACTION_ENTRY_NAME_KEY			= "name";
	private static final String ACTION_ENTRY_METHOD_KEY			= "method";
	private static final String RESULT_KEY						= "result";
	private static final String RESULT_TYPE_KEY					= "type";
	private static final String RESULT_NAME_KEY					= "name";
	private static final String ACTION_DEFAULT_ENTRY_METHOD		= "execute";

	private static final String CONFIG_FILE_KEY					= "mvc-config-file";
	private static final String DEFAULT_CONFIG_FILE				= "mvc-config.xml";
	
	private static final String ACTION_SUFFIX_KEY				= "action-suffix";
	private static final String SUFFIX_CHARACTER				= ".";
	private static final String DEFAULT_ACTION_SUFFIX			= ".action";
	
	private static final String ACTION_FILTERS_KEY				= "action-filters";
	private static final String FILTER_KEY						= "filter";
	private static final String FILTER_CLASS_KEY				= "class";
	private static final String FILTER_PATTERN_KEY				= "pattern";
	private static final String FILTER_METHODS_KEY				= "methods";
	private static final String FILTER_DEFAULT_PATTERN			= ".*";
	private static final String FILTER_DEFAULT_METHODS			= ".*";
	
	private static final String ACTION_CONV_KEY					= "action-convention";
	private static final String CONV_ENABLE_KEY					= "enable";
	private static final String CONV_DETECT_PHYSICAL_FILE_KEY	= "detect-physical-file";
	private static final String CONV_BASE_PACKAGE_KEY			= "action-base-package";
	private static final String CONV_DISPATCH_FILE_PATH_KEY		= "dispatch-file-path";
	private static final String CONV_DISPATCH_FILE_TYPE_KEY		= "dispatch-file-type";
	private static final String CONV_PHYSICAL_FILE_PATH_KEY		= "physical-file-path";
	private static final String CONV_FILE_NAME_SEPARATOR_KEY	= "file-name-separator";
	
	private static final String CONV_DEFAULT_DISPATCH_FILE_PATH	= "/WEB-INF/page";
	private static final String CONV_DEFAULT_FILE_TYPE			= "jsp";
	private static final String CONV_DEFAULT_FILE_NAME_SEPARATOR= "_";
	private static final String CONV_ACTION_PATH_NAME_SEPARATOR	= "-";
	private static final String CONV_ACTION_NAME_USUAL_ENDING	= "Action";

	private static final String GLOBAL_RESULTS_KEY				= "global-results";
	private static final String GLOBAL_EXCEPTION_MAPS_KEY		= "global-exception-mappings";
	
	private static final String ENCODING_KEY					= "encoding";
	
	private static final String EXCEPTION_MAP_KEY				= "exception-mapping";
	private static final String EXCEPTION_KEY					= "exception";
	
	private String encoding				= null;
	private String actionSuffix			= DEFAULT_ACTION_SUFFIX;
	private ActionConvention convention	= new ActionConvention();
	private Map<String, ActionResult> globalResults;
	private List<ActionException> globalExceptions;
	private List<ActionFilterInfo> filterInfoList						= new ArrayList<ActionFilterInfo>();
	private LinkedList<ActionFilter> filterList 						= new LinkedList<ActionFilter>();
	private Map<String, Map<String, ActionEntryConfig>> actionPkgMap	= new HashMap<String, Map<String, ActionEntryConfig>>();
	private Map<Method, LinkedList<ActionFilter>> filterCache			= new HashMap<Method, LinkedList<ActionFilter>>();
	private Map<Class<ActionFilter>, ActionFilter> filterMap			= new HashMap<Class<ActionFilter>, ActionFilter>();
	private Map<Class<? extends Action>, ActionConfig> convACMap		= new HashMap<Class<? extends Action>, ActionConfig>();
	
	private ServletContext context;
	
	public void init(FilterConfig filterConfig) throws ServletException
	{
		context = filterConfig.getServletContext();
		context.setAttribute(Action.Constant.APP_ATTR_CONTEXT_PATH, context.getContextPath() + PATH_SEPARATOR);
		
		/* 初始化 HttpHelper 的 Servlet Context */
		HttpHelper.initializeServletContext(context);

		String confFile = filterConfig.getInitParameter(CONFIG_FILE_KEY);
		
		if(GeneralHelper.isStrEmpty(confFile))
			confFile = DEFAULT_CONFIG_FILE;
		
		confFile = GeneralHelper.getClassResourcePath(ActionDispatcher.class, confFile);
		
		loadConfig(confFile, true, null);
		loadActionFilters();
	}

	private void loadConfig(String confFile, boolean isMainConfigFile, Set<String> incFiles) throws ServletException
	{
		try
		{
            SAXReader sr	= new SAXReader();
            Document doc	= sr.read(new File(confFile));
            Element root	= doc.getRootElement();
                
            if(isMainConfigFile)
            {
        		incFiles = new HashSet<String>();
        		incFiles.add(confFile);
        		
            	parseGlobal(root);
            }
            
        	parseInclude(root, incFiles);
        	parseActionPackage(root);
		}
		catch(Exception e)
		{
			throw new ServletException("load MVC config fail", e);
		}
	}
	
	private void parseGlobal(Element root) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		Element global = root.element(GLOBAL_KEY);
		if(global != null)
		{
			Element enc = global.element(ENCODING_KEY);
			if(enc != null)
			{
				String str = enc.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
					encoding = str;
			}
			
			Element acSuffix = global.element(ACTION_SUFFIX_KEY);
			if(acSuffix != null)
			{
				String str = acSuffix.getTextTrim();
				if(GeneralHelper.isStrNotEmpty(str))
				{
					if(str.startsWith(SUFFIX_CHARACTER))
						actionSuffix = str;
					else
						actionSuffix = SUFFIX_CHARACTER + str;
				}
			}
			
			Element acConv = global.element(ACTION_CONV_KEY);
			if(acConv != null)
				parseActionConvention(acConv);
			
			Element acFilters = global.element(ACTION_FILTERS_KEY);
			if(acFilters != null)
				parseActionFilters(acFilters);
			
			Element gResults = global.element(GLOBAL_RESULTS_KEY);
			if(gResults != null)
				globalResults = parseResults(gResults);
			
			Element gExceptionMaps = global.element(GLOBAL_EXCEPTION_MAPS_KEY);
			if(gExceptionMaps != null)
				globalExceptions = parseExceptionLists(gExceptionMaps);
		}
		
		ensureGlobalResultNone();
	}

	private void parseActionConvention(Element acConv)
	{
		String enable		= acConv.attributeValue(CONV_ENABLE_KEY);
		String detect		= acConv.attributeValue(CONV_DETECT_PHYSICAL_FILE_KEY);
		String basePkg		= acConv.attributeValue(CONV_BASE_PACKAGE_KEY);
		String dispatchPath	= acConv.attributeValue(CONV_DISPATCH_FILE_PATH_KEY);
		String fileType		= acConv.attributeValue(CONV_DISPATCH_FILE_TYPE_KEY);
		String physicalPath	= acConv.attributeValue(CONV_PHYSICAL_FILE_PATH_KEY);
		String nameSep		= acConv.attributeValue(CONV_FILE_NAME_SEPARATOR_KEY);
		
		convention.enable		= GeneralHelper.str2Boolean(enable, true);
		convention.detect		= GeneralHelper.str2Boolean(detect, true);
		convention.basePackage	= GeneralHelper.safeString(basePkg);	
		convention.dispatchPath = HttpHelper.ensurePath(dispatchPath, CONV_DEFAULT_DISPATCH_FILE_PATH);
		convention.physicalPath = HttpHelper.ensurePath(physicalPath, convention.dispatchPath);
		
		if(GeneralHelper.isStrNotEmpty(fileType))
			convention.fileType = fileType;
		else
			convention.fileType = CONV_DEFAULT_FILE_TYPE;
		
		if(GeneralHelper.isStrNotEmpty(nameSep))
			convention.separator = nameSep.substring(0, 1);
		else
			convention.separator = CONV_DEFAULT_FILE_NAME_SEPARATOR;	
	}

	@SuppressWarnings("unchecked")
	private void parseActionFilters(Element acFilters) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		List<Element> filters = acFilters.elements(FILTER_KEY);
		
		for(Element filter : filters)
		{
			String clazz	= filter.attributeValue(FILTER_CLASS_KEY);
			String pattern	= filter.attributeValue(FILTER_PATTERN_KEY);
			String methods	= filter.attributeValue(FILTER_METHODS_KEY);
			
			if(GeneralHelper.isStrEmpty(clazz))
				throw new RuntimeException("parse action filter fail ('class' attribute must be set)");
			if(GeneralHelper.isStrEmpty(pattern))
				pattern = FILTER_DEFAULT_PATTERN;
			if(GeneralHelper.isStrEmpty(methods))
				methods = FILTER_DEFAULT_METHODS;
			
			ActionFilterInfo info = new ActionFilterInfo();
			info.afClass = (Class<ActionFilter>)Class.forName(clazz);
			info.pattern = Pattern.compile(pattern);
			info.methods = Pattern.compile(methods);
			
			filterInfoList.add(info);
		
			ActionFilter f = filterMap.get(info.afClass);
			
			if(f == null)
			{
				f = info.afClass.newInstance();
				f.init();
				
				filterList.addLast(f);
				filterMap.put(info.afClass, f);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<ActionException> parseExceptionLists(Element exceptionMaps) throws ClassNotFoundException
	{
		List<ActionException> exceptions = new ArrayList<ActionException>();
		List<Element> maps = exceptionMaps.elements(EXCEPTION_MAP_KEY);
		
		for(Element map : maps)
		{
			ActionException ae = new ActionException();
			
			String clazz	= map.attributeValue(EXCEPTION_KEY);
			ae.result		= map.attributeValue(RESULT_KEY);			
			
			if(GeneralHelper.isStrEmpty(clazz))
				ae.exception = Exception.class;
			else
				ae.exception = (Class<? extends Exception>)Class.forName(clazz);
			
			if(ae.result == null)
				ae.result = Action.EXCEPTION;
			
			exceptions.add(ae);
		}
		
		return exceptions.isEmpty() ? null : exceptions;
	}

	private void ensureGlobalResultNone()
	{
		if(globalResults == null)
			globalResults = new HashMap<String, ActionResult>();
		
		if(!globalResults.containsKey(Action.NONE))
			globalResults.put(Action.NONE, new ActionResult(Action.NONE, Action.ResultType.FINISH));
	}

	@SuppressWarnings("unchecked")
	private void parseInclude(Element root, Set<String> incFiles) throws ServletException
	{
		List<Element> includes = root.elements(INCLUDE_KEY);
		for(Element inc : includes)
		{
			String fileName	= inc.attributeValue(INCLUDE_FILE_KEY);
			String incFile	= GeneralHelper.getClassResourcePath(ActionDispatcher.class, fileName);
			
			if(incFile == null)
				throw new ServletException(String.format("include file '%s' not found", fileName));
			else
			{
				if(incFiles.contains(incFile))
					continue;
				else
					incFiles.add(incFile);
			}
			
			loadConfig(incFile, false, incFiles);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseActionPackage(Element root) throws ClassNotFoundException, SecurityException, NoSuchMethodException
	{
		List<Element> acsList = root.elements(ACTIONS_KEY);
		for(Element actions : acsList)
		{
			String path = actions.attributeValue(ACTIONS_PATH_KEY);
			path		= HttpHelper.ensurePath(path, PATH_SEPARATOR);
			
			Map<String, ActionEntryConfig> actionEntryMap = actionPkgMap.get(path);
			
			if(actionEntryMap == null)
			{
				actionEntryMap = new HashMap<String, ActionEntryConfig>();
				actionPkgMap.put(path, actionEntryMap);
			}
			
			parseAction(actions, actionEntryMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseAction(Element actions, Map<String, ActionEntryConfig> actionEntryMap) throws ClassNotFoundException, SecurityException, NoSuchMethodException
	{
		List<Element> acList = actions.elements(ACTION_KEY);
		for(Element action : acList)
		{
			ActionConfig ac = new ActionConfig();
			ac.name			= action.attributeValue(ACTION_NAME_KEY);
			String clazz	= action.attributeValue(ACTION_CLASS_KEY);
			
			if(clazz == null)
				ac.acClass	= ActionSupport.class;
			else
				ac.acClass	= (Class<? extends Action>)Class.forName(clazz);
			
			ac.results		= parseResults(action);
			ac.exceptions	= parseExceptionLists(action);
			
			parseActionEntry(action, ac, actionEntryMap);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void parseActionEntry(Element action, ActionConfig ac, Map<String, ActionEntryConfig> actionEntryMap) throws SecurityException, NoSuchMethodException, ClassNotFoundException
	{
		List<Element> aecList = action.elements(ACTION_ENTRY_KEY);
		for(Element entry : aecList)
		{
			String name 	= entry.attributeValue(ACTION_ENTRY_NAME_KEY);
			String method	= entry.attributeValue(ACTION_ENTRY_METHOD_KEY);
			
			String entryName	= GeneralHelper.isStrEmpty(name)	? ac.name : ac.name + ACTION_ENTRY_SEPARATOR + name;
			String entryMethod	= GeneralHelper.isStrEmpty(method)	? 
									(GeneralHelper.isStrEmpty(name)	? ACTION_DEFAULT_ENTRY_METHOD : name) : method;
									
			Method m = ac.acClass.getMethod(entryMethod);
			if(!String.class.isAssignableFrom(m.getReturnType()) || !BeanHelper.isPublicInstanceMethod(m))
			{
				String msg = String.format("invalid action entry method '%s'", m);
				throw new RuntimeException(msg);
			}
			
			actionEntryMap.put(entryName, new ActionEntryConfig(entryName, m, ac, parseResults(entry), parseExceptionLists(entry)));
		}
		
		if(actionEntryMap.get(ac.name) == null)
			actionEntryMap.put(ac.name, new ActionEntryConfig(ac.name, ac.acClass.getMethod(ACTION_DEFAULT_ENTRY_METHOD), ac));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ActionResult> parseResults(Element rsElement)
	{
		Map<String, ActionResult> map = new HashMap<String, ActionResult>();
		List<Element> results = rsElement.elements(RESULT_KEY);
		
		for(Element result : results)
		{
			ActionResult ars = new ActionResult();
			
			ars.name	= result.attributeValue(RESULT_NAME_KEY);
			String type	= result.attributeValue(RESULT_TYPE_KEY);
			ars.path	= result.getTextTrim();
			
			if(ars.name == null)
				ars.name = Action.SUCCESS;
			
			if(type == null || type.equals(Action.ResultType.DEFAULT.toString()))
			{
				if(!ars.name.equals(Action.NONE))
					ars.type = Action.ResultType.DISPATCH;
				else
					ars.type = Action.ResultType.FINISH;
			}
			else
				ars.type = Action.ResultType.valueOf(type.toUpperCase());
			
			map.put(ars.name, ars);
		}
		
		return map.isEmpty() ? null : map;
	}
	
	private void loadActionFilters()
	{
		if(!filterInfoList.isEmpty())
		{
			Collection<Map<String, ActionEntryConfig>> packages	= actionPkgMap.values();
			
			for(Map<String, ActionEntryConfig> pkg : packages)
			{
				Collection<ActionEntryConfig> aecs = pkg.values();
				
				for(ActionEntryConfig aec : aecs)
					loadActionFilterCache(aec);
			}
		}
	}

	private void loadActionFilterCache(ActionEntryConfig aec)
	{
		LinkedList<ActionFilter> filters = filterCache.get(aec.method);
		
		if(filters == null)
		{
			filters = matchActionFilters(aec);
			tryPutFilterCache(aec.method, filters);
		}
	}

	private LinkedList<ActionFilter> matchActionFilters(ActionEntryConfig aec)
	{
		Set<ActionFilter> tmpFilterSet	 = new HashSet<ActionFilter>();
		LinkedList<ActionFilter> filters = new LinkedList<ActionFilter>();
		
		for(ActionFilterInfo info : filterInfoList)
		{
			Matcher m = info.pattern.matcher(aec.container.acClass.getName());
			
			if(m.matches())
			{
				m = info.methods.matcher(aec.method.getName());
				if(m.matches())
				{
					ActionFilter filter	= filterMap.get(info.afClass);
					
					if(!tmpFilterSet.contains(filter))
					{
						filters.add(filter);
						tmpFilterSet.add(filter);
					}
				}
			}
		}
		
		return filters;
	}

	private void tryPutFilterCache(Method method, LinkedList<ActionFilter> filters)
	{
		if(!filters.isEmpty())
		{
    		Collection<LinkedList<ActionFilter>> fs = filterCache.values();
    		
    		for(LinkedList<ActionFilter> f : fs)
    		{
    			if(filters.equals(f))
    			{
    				filters = f;
    				break;
    			}
    		}
    		
    		GeneralHelper.syncTryPut(filterCache, method, filters);
		}
	}

	public void destroy()
	{
		while(!filterList.isEmpty())
			filterList.removeLast().destroy();
		
		convACMap			= null;
		filterList			= null;
		filterInfoList		= null;
		filterMap			= null;
		filterCache			= null;
		actionPkgMap		= null;
		globalResults		= null;
		globalExceptions	= null;
		
		/* 释放 HttpHelper 的 Servlet Context */
		HttpHelper.unInitializeServletContext();
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request	 = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		if(encoding != null)
		{
    		request.setCharacterEncoding(encoding);
    		response.setCharacterEncoding(encoding);
		}
		
		String reqPath	= request.getServletPath();
		
		if(reqPath.endsWith(actionSuffix))
		{
			String actionPath = reqPath.substring(0, reqPath.length() - actionSuffix.length());
			dispatchAction(request, response, new ActionPackage(actionPath));
		}
		else
			chain.doFilter(request, response);
	}

	private void dispatchAction(HttpServletRequest request, HttpServletResponse response, ActionPackage pkg) throws ServletException, IOException
	{
		ActionEntryConfig aec = null;
		
		try
		{
			aec = extractActionEntryConfig(pkg, true);
		}
		catch(Exception e)
		{
			String msg = String.format("Extract Action Convention '%s' fail", pkg);
			throw new ServletException(msg, e);
		}
		
		if(aec == null)
		{
			String msg = String.format("Action Entry '%s' not found", pkg);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			return;
		}
		
		Action action = null;
		
		try
		{
			action = createAction(request, response, aec);
		}
		catch(Exception e)
		{
			String msg = String.format("Instantiate Action '%s (%s)' fail", pkg, aec.container.acClass.getName());
			throw new ServletException(msg, e);
		}
		
		try
		{
			String result	= executeAction(request, response, aec, action);
			ActionResult rs	= findResult(request, pkg, aec, result);
			
			if(rs != null)
				processResult(request, response, pkg, rs, action);
			else
			{
				String msg = String.format("Result Name '%s' in Action Entry '%s' not found", result, pkg);
				throw new ServletException(msg);
			}
		}
		catch(Exception e)
		{
			throw new ServletException(e);
		}
	}

	private ActionEntryConfig extractActionEntryConfig(ActionPackage pkg, boolean firstTime) throws Exception
	{
		ActionEntryConfig aec = null;
		Map<String, ActionEntryConfig> actionEntryMap = actionPkgMap.get(pkg.path);
        		
		if(actionEntryMap != null)
			aec = actionEntryMap.get(pkg.name);
		
		if(aec == null & firstTime && convention.enable)
		{
			extractActionEntryConvention(pkg);
			aec = extractActionEntryConfig(pkg, false);
		}
		
		return aec;
	}

	private String executeAction(HttpServletRequest request, HttpServletResponse response, ActionEntryConfig aec, Action action) throws Exception
	{
		String result = null;
		LinkedList<ActionFilter> filters = filterCache.get(aec.method);

		try
		{
			if(filters == null)
				result = ActionExecutor.execute(action, aec.method);
			else
			{
				ActionExecutor executor = new ActionExecutor(filters, action, aec.method, context, request, response);
				result = executor.invoke();
			}
		}
		catch(Exception e)
		{
			request.setAttribute(Action.Constant.REQ_ATTR_EXCEPTION, e);

			if(aec.exceptions != null)
				result = processActionException(request, aec.exceptions, e);
			if(result == null && aec.container.exceptions != null)
				result = processActionException(request, aec.container.exceptions, e);
			if(result == null && globalExceptions != null)
				result = processActionException(request, globalExceptions, e);
			if(result == null)
				throw e;
		}
		
		return result;
	}
	
	private String processActionException(HttpServletRequest request, List<ActionException> aes, Exception e)
	{
		String result = null;

		for(ActionException ae : aes)
		{
			if(ae.exception.isAssignableFrom(e.getClass()))
			{
				result = ae.result;		
				break;
			}
		}
		
		return result;
	}

	private Action createAction(HttpServletRequest request, HttpServletResponse response, ActionEntryConfig aec) throws InstantiationException, IllegalAccessException
	{
		Action action = aec.container.acClass.newInstance();
		
		if(action instanceof ActionContextAware)
		{
			ActionContextAware aw = (ActionContextAware)action;
			aw.setServletContext(context);
			aw.setRequest(request);
			aw.setResponse(response);
		}
		
		parseFormBean(aec, action, request.getParameterMap());
		
		return action;
	}

	private ActionResult findResult(HttpServletRequest request, ActionPackage pkg, ActionEntryConfig aec, String result)
	{
		ActionResult rs = null;

		if(aec.results != null)
			rs = aec.results.get(result);
		if(rs == null && aec.container.results != null)
			rs = aec.container.results.get(result);
		if(rs == null && globalResults != null)
			rs = globalResults.get(result);
		if(rs == null && convention.enable)
			rs = makeConventionResult(result, request, pkg, aec);
		
		return rs;
	}
	
	private void processResult(HttpServletRequest request, HttpServletResponse response, ActionPackage pkg, ActionResult ars, Action action) throws ServletException, IOException
	{
		switch(ars.type)
		{
		case DISPATCH:
			request.setAttribute(Action.Constant.REQ_ATTR_ACTION, action);
			request.setAttribute(Action.Constant.REQ_ATTR_BASE_PATH, HttpHelper.getRequestBasePath(request));
			RequestDispatcher rd = request.getRequestDispatcher(ars.path);
			
			if(rd != null)
				rd.forward(request, response);
			else
			{
				String msg = String.format("Dispatch URL '%s' not found", ars.path);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			}
			
			break;
		case REDIRECT:
			response.sendRedirect(ars.path);
			break;
		case CHAIN:
			request.setAttribute(Action.Constant.REQ_ATTR_ACTION, action);
			dispatchChainAction(request, response, pkg, ars.path);
			break;
		case FINISH:
			break;
		default:
			assert false;
		}
	}
	
	private static final <T> void parseFormBean(ActionEntryConfig aec, Action action, Map<String, T> paramMap)
	{
		if(aec.formBeanAttr != null)
		{
			if(aec.formBeanAttr.property != null)
			{
				Object bean = BeanHelper.createBean(aec.formBeanAttr.property.getPropertyType(), paramMap);
				BeanHelper.setProperty(action, aec.formBeanAttr.property, bean);
			}
			else if(aec.formBeanAttr.field != null)
			{
				Object bean = BeanHelper.createBean(aec.formBeanAttr.field.getType(), paramMap);
				BeanHelper.setFieldValue(action, aec.formBeanAttr.field, bean);
			}
			else
				BeanHelper.setPropertiesOrFieldValues(action, paramMap);
		}
	}

	private void dispatchChainAction(HttpServletRequest request, HttpServletResponse response, ActionPackage currentPkg, String path) throws ServletException, IOException
	{
		ActionPackage pkg = new ActionPackage(path, currentPkg.path);
		dispatchAction(request, response, pkg);
	}
	
	private static class ActionFilterInfo
	{
		Class<ActionFilter> afClass;
		Pattern pattern;
		Pattern methods;
	}
	
	private static class ActionConfig
	{
		String name;
		Class<? extends Action> acClass;
		Map<String, ActionResult> results;
		List<ActionException> exceptions;
	}
	
	@SuppressWarnings("unused")
	private static class ActionEntryConfig
	{
		String name;
		Method method;
		ActionConfig container;
		Map<String, ActionResult> results;
		List<ActionException> exceptions;
		FormBeanAttr formBeanAttr;
		
		private static class FormBeanAttr
		{
			PropertyDescriptor property;
			Field field;
			
			FormBeanAttr()
			{
			}
			
			FormBeanAttr(PropertyDescriptor pd)
			{
				this.property = pd;
			}

			FormBeanAttr(Field f)
			{
				this.field = f;
			}
		}
		
		ActionEntryConfig(String name, Method method, ActionConfig container)
		{
			this(name, method, container, null, null);
		}

		
		ActionEntryConfig
			(	String name, Method method, ActionConfig container,
				Map<String, ActionResult> results, List<ActionException> exceptions	)
		{
			this.name		= name;
			this.method		= method;
			this.container	= container;
			this.results	= results;
			this.exceptions	= exceptions;
			
			analysisFormBeanAttr();
		}

		private void analysisFormBeanAttr()
		{
			FormBean formBean = method.getAnnotation(FormBean.class);
			
			if(formBean == null)
				formBean = container.acClass.getAnnotation(FormBean.class);
			
			if(formBean != null)
			{
				String value = formBean.value();
				
				if(GeneralHelper.isStrEmpty(value))
					formBeanAttr = new FormBeanAttr();
				else
				{
					Class<?> stopClass		= ActionSupport.class.isAssignableFrom(container.acClass) ? ActionSupport.class : Object.class;
					PropertyDescriptor pd	= BeanHelper.getPropDescByName(container.acClass, stopClass, value);
					Method setter			= BeanHelper.getPropertyWriteMethod(pd);

					if(setter != null)
						formBeanAttr = new FormBeanAttr(pd);
					else
					{
						Field field = BeanHelper.getInstanceFiledByName(container.acClass, stopClass, value);
						
						if(field != null)
							formBeanAttr = new FormBeanAttr(field);
					}
					
					if(formBeanAttr == null)
					{
						String msg = String.format("Parse @FormBean in '%s#%s()' -> no property or field named '%s'", 
									 container.acClass.getName(), method.getName(), value);
				
						throw new RuntimeException(msg);
					}
				}
			}
		}
	}
	
	private static class ActionResult
	{
		String name;
		Action.ResultType type;
		String path;
		
		ActionResult()
		{
			
		}

		ActionResult(String name, ResultType type)
		{
			this(name, type, null);
		}

		ActionResult(String name, ResultType type, String path)
		{
			this.name = name;
			this.type = type;
			this.path = path;
		}
	}
	
	private static class ActionException
	{
		Class<? extends Exception> exception;
		String result;
	}
	
	private static class ActionPackage
	{
		String path;
		String name;

		ActionPackage(String actionPath)
		{
			this(actionPath, null);
		}
		
		ActionPackage(String actionPath, String currentPath)
		{
			int sepIndex = actionPath.lastIndexOf(PATH_SEPARATOR);
			
			if(sepIndex != -1)
			{
				path = actionPath.substring(0, sepIndex + 1);
				if(currentPath != null && path.startsWith(CURRENT_PATH_PREFIX))
					path = path.replace(CURRENT_PATH_PREFIX, currentPath);
				if(!path.startsWith(PATH_SEPARATOR))
					path = PATH_SEPARATOR + path;
				
				if(sepIndex < actionPath.length() - 1)
					name = actionPath.substring(sepIndex + 1, actionPath.length());
			}
			else
			{
				path = PATH_SEPARATOR;
				name = actionPath;
			}
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			if(path != null)
				sb.append(path);
			if(name != null)
				sb.append(name);
			
			return sb.toString();
		}
	}
	
	/* **************************************************************************************************** */
	//											Action Convention											//
	/* **************************************************************************************************** */

	private void extractActionEntryConvention(ActionPackage pkg) throws Exception
	{
		ActionEntryDesc desc	= ActionEntryDesc.generate(convention.basePackage, pkg);
		ActionEntryConfig aec	= parseActionEntry(desc);
		
		tryPutActionEntryConfig(pkg, aec);
		
		return;
	}

	private void tryPutActionEntryConfig(ActionPackage pkg, ActionEntryConfig aec)
	{
		loadActionFilterCache(aec);
		
		synchronized(actionPkgMap)
		{
			Map<String, ActionEntryConfig> actionEntryMap = actionPkgMap.get(pkg.path);
			
			if(actionEntryMap == null)
			{
				actionEntryMap = new HashMap<String, ActionEntryConfig>();
				actionPkgMap.put(pkg.path, actionEntryMap);
			}
			
			GeneralHelper.syncTryPut(actionEntryMap, pkg.name, aec);
		}
	}

	private ActionEntryConfig parseActionEntry(ActionEntryDesc desc)
	{
		checkConvActionConfigMap(desc);
		ActionConfig ac = convACMap.get(desc.clazz);
		
		ActionEntryConfig aec = new ActionEntryConfig(desc.entryName, desc.method, ac);
		aec.results		= parseResults(desc.method);
		aec.exceptions	= parseExceptionLists(desc.method);
		
		return aec;
	}

	private void checkConvActionConfigMap(ActionEntryDesc desc)
	{
		if(!convACMap.containsKey(desc.clazz))
		{
			ActionConfig ac = new ActionConfig();
			ac.name			= desc.actionName;
			ac.acClass		= desc.clazz;
			ac.results		= parseResults(desc.clazz);
			ac.exceptions	= parseExceptionLists(desc.clazz);

			GeneralHelper.syncTryPut(convACMap, ac.acClass, ac);
		}
	}

	private Map<String, ActionResult> parseResults(AnnotatedElement ae)
	{
		Map<String, Result> rsMap		 = parseResultAnnotations(ae);
		Map<String, ActionResult> arsMap = parseActionResultMap(rsMap);
		
		return arsMap.isEmpty() ? null : arsMap;
	}

	private Map<String, Result> parseResultAnnotations(AnnotatedElement ae)
	{
		Map<String, Result> map = new HashMap<String, Result>();
		Result result = ae.getAnnotation(Result.class);
		
		if(result != null)
			GeneralHelper.tryPut(map, result.value(), result);
		
		Results results = ae.getAnnotation(Results.class);
		
		if(results != null)
		{
			Result[] rs = results.value();
			for(Result r : rs)
				GeneralHelper.tryPut(map, r.value(), r);
		}
		
		return map;
	}

	private Map<String, ActionResult> parseActionResultMap(Map<String, Result> rsMap)
	{
		Map<String, ActionResult> map	= new HashMap<String, ActionResult>();
		Collection<Result> results		= rsMap.values();
		
		for(Result result : results)
		{
			ActionResult ars = new ActionResult();
			
			ars.name = result.value();
			ars.type = result.type();
			ars.path = result.path();
			
			if(ars.type == Action.ResultType.DEFAULT)
			{
				if(!ars.name.equals(Action.NONE))
					ars.type = Action.ResultType.DISPATCH;
				else
					ars.type = Action.ResultType.FINISH;
			}
			
			map.put(ars.name, ars);
		}
		
		return map;
	}

	private List<ActionException> parseExceptionLists(AnnotatedElement ae)
	{
		List<ExceptionMapping> emList	= parseExceptionMappingAnnotations(ae);
		List<ActionException> aeList	= parseActionExceptionList(emList);
		
		return aeList.isEmpty() ? null : aeList;
	}

	private List<ExceptionMapping> parseExceptionMappingAnnotations(AnnotatedElement ae)
	{
		List<ExceptionMapping> list	= new ArrayList<ExceptionMapping>();
		ExceptionMapping em			= ae.getAnnotation(ExceptionMapping.class);
		
		if(em != null)
			list.add(em);
		
		ExceptionMappings ems = ae.getAnnotation(ExceptionMappings.class);
		
		if(ems != null)
		{
			ExceptionMapping[] emArr = ems.value();
			for(ExceptionMapping em2 : emArr)
				list.add(em2);
		}
		
		return list;
	}

	private List<ActionException> parseActionExceptionList(List<ExceptionMapping> emList)
	{
		List<ActionException> exceptions = new ArrayList<ActionException>();
		
		for(ExceptionMapping em : emList)
		{
			ActionException ae = new ActionException();
			
			ae.exception = em.value();
			ae.result	 = em.result();			
			
			exceptions.add(ae);
		}
		
		return exceptions;
	}

	private static class ActionConvention
	{
		boolean enable;
		boolean detect;
		String basePackage;
		String dispatchPath;
		String physicalPath;
		String fileType;
		String separator;
	}
	
	private static class ActionEntryDesc
	{
		String actionPath;
		String actionName;
		String entryName;
		String className;
		String methodName;
		
		Class<? extends Action> clazz;
		Method method;
		
		static ActionEntryDesc generate(String basePackage, ActionPackage pkg) throws SecurityException, NoSuchMethodException
		{
			ActionEntryDesc desc = new ActionEntryDesc();
			desc.parseBasicInfo(basePackage, pkg);
			desc.parseEntryInfo();

			return desc;
		}

		private void parseBasicInfo(String basePackage, ActionPackage pkg)
		{
			this.actionPath	= pkg.path;
			this.entryName	= pkg.name;
			
			StringBuilder path	= new StringBuilder(basePackage);
			String subPath		= pkg.toString();
			
			if(!subPath.startsWith(PATH_SEPARATOR))
				path.append(PATH_SEPARATOR);
			
			path.append(subPath);
			
			String[] paths	= GeneralHelper.splitStr(path.toString(), PATH_SEPARATOR + SUFFIX_CHARACTER);
			int index		= paths.length - 1;
			String[] entry	= GeneralHelper.splitStr(this.entryName, ACTION_ENTRY_SEPARATOR);
			this.actionName	= entry[0];
			
			if(entry.length == 1)
				this.methodName = ACTION_DEFAULT_ENTRY_METHOD;
			else
			{
				paths[index]		= entry[0];
				String[] halfNames	= GeneralHelper.splitStr(entry[1], CONV_ACTION_PATH_NAME_SEPARATOR);
				StringBuilder name	= new StringBuilder();
				
				for(int j = 0; j < halfNames.length; j++)
				{
					String part = halfNames[j];
					
					if(j == 0)
						name.append(Character.toLowerCase(part.charAt(0)));
					else
						name.append(Character.toUpperCase(part.charAt(0)));

					name.append(part.substring(1));
				}
				
				this.methodName = name.toString();
			}
			
			StringBuilder name	= new StringBuilder();
			
			for(int i = 0; i <= index; i++)
			{
				String str = paths[i];
				String[] halfNames = GeneralHelper.splitStr(str, CONV_ACTION_PATH_NAME_SEPARATOR);
				
				for(int j = 0; j < halfNames.length; j++)
				{
					String part = halfNames[j];
					
					if(i < index)
						name.append(part.toLowerCase());
					else
					{
						name.append(Character.toUpperCase(part.charAt(0)));
						name.append(part.substring(1));
					}
				}
				
				if(i < index)
					name.append(SUFFIX_CHARACTER);
			}
			
			this.className = name.toString();
		}

		@SuppressWarnings("unchecked")
		private void parseEntryInfo() throws SecurityException, NoSuchMethodException
		{
			Class<?> clazz = GeneralHelper.classForName(className);
			if(clazz == null && !className.endsWith(CONV_ACTION_NAME_USUAL_ENDING))
				clazz = GeneralHelper.classForName(className + CONV_ACTION_NAME_USUAL_ENDING);

			if(clazz != null && Action.class.isAssignableFrom(clazz) && BeanHelper.isPublicNotAbstractClass(clazz))
			{
				Method method = clazz.getMethod(methodName);
				if(!String.class.isAssignableFrom(method.getReturnType()) || !BeanHelper.isPublicInstanceMethod(method))
				{
					String msg = String.format("invalid action entry method '%s' for '%s%s'", method, actionPath, entryName);
					throw new RuntimeException(msg);
				}
				
				this.clazz	= (Class<? extends Action>)clazz;
				this.method	= method;
			}
			else
			{
				String msg = String.format("invalid action class '%s[%s]' for '%s%s'", className, CONV_ACTION_NAME_USUAL_ENDING, actionPath, entryName);
				throw new RuntimeException(msg);
			}
		}
	}
	
	private ActionResult makeConventionResult(String result, HttpServletRequest request, ActionPackage pkg, ActionEntryConfig aec)
	{
		final String SUCCESS_ENDS = convention.separator + Action.SUCCESS;
		
		String pkgNamePart	= pkg.name.replace(ACTION_ENTRY_SEPARATOR, convention.separator);
		String fileNamePart	= pkgNamePart + convention.separator + result;
		String fileName		= fileNamePart + SUFFIX_CHARACTER + convention.fileType;
		String dispatchPath	= null;
		
		if(!fileNamePart.endsWith(SUCCESS_ENDS))
			dispatchPath = makeDispatchPath(fileName, request, pkg, aec, convention.detect);
		else
		{
			dispatchPath = makeDispatchPath(fileName, request, pkg, aec, true);
			
			if(dispatchPath == null)
			{
				String fileNamePart2 = fileNamePart.substring(0, fileNamePart.length() - SUCCESS_ENDS.length());
				String fileName2	 = fileNamePart2 + SUFFIX_CHARACTER + convention.fileType;
				dispatchPath		 = makeDispatchPath(fileName2, request, pkg, aec, true);
				
				if(dispatchPath == null && !convention.detect)
					dispatchPath = makeDispatchPath(fileName, request, pkg, aec, false);
			}
		}
		
		if(dispatchPath != null)
		{
			tryPutActionResult(result, aec, dispatchPath);
			return aec.results.get(result);
		}
		else
		{
			String pkgPath		= pkg.path.substring(1);
			String physicalPath = convention.physicalPath + pkgPath + fileName;
			String msg = String.format("physical target file '%s' not found for Action Result '%s' (%s)", physicalPath, result, pkg);
			throw new RuntimeException(msg);
		}

	}

	private String makeDispatchPath(String fileName, HttpServletRequest request, ActionPackage pkg, ActionEntryConfig aec, boolean detect)
	{
		String pkgPath		= pkg.path.substring(1);
		String dispatchPath	= convention.dispatchPath + pkgPath + fileName;
		
		if(detect)
		{
			String physicalPath = convention.physicalPath + pkgPath + fileName;
			String absolutePath = HttpHelper.getRequestRealPath(request, physicalPath);
			
			if(!(new File(absolutePath).isFile()))
				return null;
		}
		
		return dispatchPath;
	}

	private void tryPutActionResult(String result, ActionEntryConfig aec, String dispatchPath)
	{
		synchronized(aec)
		{
			if(aec.results == null)
				aec.results = new HashMap<String, ActionResult>();
			
			GeneralHelper.syncTryPut(aec.results, result, new ActionResult(result, Action.ResultType.DISPATCH, dispatchPath));
		}
	}
}
