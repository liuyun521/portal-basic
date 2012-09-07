package com.bruce.mvc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

import org.dom4j.*;
import org.dom4j.io.SAXReader;

import com.bruce.util.GeneralHelper;
import com.bruce.util.http.HttpHelper;

/**
 * 
 * MVC 前端控制器，实现为 {@link Filter}
 *
 */
public class ActionDispatcher implements Filter
{
	private static final String GLOBAL_KEY					= "global";
	private static final String INCLUDE_KEY					= "include";
	private static final String INCLUDE_FILE_KEY			= "file";
	private static final String ACTIONS_KEY					= "actions";
	private static final String ACTIONS_PATH_KEY			= "path";
	private static final String ACTIONS_PATH_SEPARATOR		= "/";
	private static final String ACTIONS_CURRENT_PATH_PREFIX	= "./";
	private static final String ACTION_KEY					= "action";
	private static final String ACTION_NAME_KEY				= "name";
	private static final String ACTION_CLASS_KEY			= "class";
	private static final String RESULT_KEY					= "result";
	private static final String RESULT_TYPE_KEY				= "type";
	private static final String RESULT_NAME_KEY				= "name";

	private static final String CONFIG_FILE_KEY				= "mvc-config-file";
	private static final String DEFAULT_CONFIG_FILE			= "mvc-config.xml";
	
	private static final String ACTION_SUFFIX_KEY			= "action-suffix";
	private static final String STUFF_CHARACTER				= ".";
	private static final String DEFAULT_ACTION_STUFF		= ".action";
	
	private static final String ACTION_FILTERS_KEY			= "action-filters";
	private static final String FILTER_KEY					= "filter";
	private static final String FILTER_CLASS_KEY			= "class";
	private static final String FILTER_PATTERN_KEY			= "pattern";
	
	private static final String GLOBAL_RESULTS_KEY			= "global-results";
	private static final String GLOBAL_EXCEPTION_MAPS_KEY	= "global-exception-mappings";
	
	private static final String ENC_KEY						= "encoding";
	
	private static final String EXCEPTION_MAP_KEY			= "exception-mapping";
	private static final String EXCEPTION_KEY				= "exception";
	private static final String EXCEPTION_RESULT_KEY		= RESULT_KEY;
	
	private static final String REQ_ATTR_ACTION				= "__action";
	private static final String REQ_ATTR_EXCEPTION			= "__exception";
	
	private String encoding;
	private String actionStuff						= DEFAULT_ACTION_STUFF;
	private List<ActionFilterInfo> filterInfoList	= new ArrayList<ActionFilterInfo>();
	private LinkedList<ActionFilter> filterList 	= new LinkedList<ActionFilter>();
	private Map<String, ActionResult> globalResults;
	private List<ActionException> globalExceptions;
	private Map<String, Map<String, ActionConfig>> actionPkgMap					= new HashMap<String, Map<String, ActionConfig>>();
	private Map<Class<? extends Action>, LinkedList<ActionFilter>> filterCache	= new HashMap<Class<? extends Action>, LinkedList<ActionFilter>>();
	
	private ServletContext context;
	
	public void init(FilterConfig filterConfig) throws ServletException
	{
		context = filterConfig.getServletContext();
		/* 初始化 HttpHelper 的 Servlet Context */
		HttpHelper.initializeServletContext(context);

		String confFile = filterConfig.getInitParameter(CONFIG_FILE_KEY);
		
		if(GeneralHelper.isStrEmpty(confFile))
			confFile = DEFAULT_CONFIG_FILE;
		
		confFile = GeneralHelper.getClassResourcePath(ActionDispatcher.class, confFile);
		
		loadConfig(confFile, true);
		loadActionFilters();
	}

	private void loadConfig(String confFile, boolean parseGlobalElement) throws ServletException
	{
		try
		{
            SAXReader sr	= new SAXReader();
            Document doc	= sr.read(new File(confFile));
            Element root	= doc.getRootElement();
                
            if(parseGlobalElement)
            	parseGlobal(root);
                
        	parseInclude(root);
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
			Element enc = global.element(ENC_KEY);
			if(enc != null)
				encoding = enc.getTextTrim();
			
			Element acStuff = global.element(ACTION_SUFFIX_KEY);
			if(acStuff != null)
			{
				actionStuff = acStuff.getTextTrim();
				if(!actionStuff.startsWith(STUFF_CHARACTER))
					actionStuff	= STUFF_CHARACTER + actionStuff;
			}
			
			Element acFilters = global.element(ACTION_FILTERS_KEY);
			if(acFilters != null)
				parseActionFilters(acFilters);
			
			Element gResults = global.element(GLOBAL_RESULTS_KEY);
			if(gResults != null)
				globalResults = parseResults(gResults);
			
			Element gExceptionMaps = global.element(GLOBAL_EXCEPTION_MAPS_KEY);
			if(gExceptionMaps != null)
				globalExceptions = parseExceptionMaps(gExceptionMaps);

		}
	}

	@SuppressWarnings("unchecked")
	private void parseActionFilters(Element acFilters) throws ClassNotFoundException
	{
		List<Element> filters = acFilters.elements(FILTER_KEY);
		
		for(Element filter : filters)
		{
			String clazz	= filter.attributeValue(FILTER_CLASS_KEY);
			String pattern	= filter.attributeValue(FILTER_PATTERN_KEY);
			
			if(GeneralHelper.isStrEmpty(clazz) || GeneralHelper.isStrEmpty(pattern))
				throw new RuntimeException("parse action filters fail (filter's 'class' or 'pattern' attribute value invalid)");
			
			ActionFilterInfo info = new ActionFilterInfo();
			info.afClass	= (Class<ActionFilter>)Class.forName(clazz);
			info.pattern	= Pattern.compile(pattern);
			
			filterInfoList.add(info);
		}
	}

	@SuppressWarnings("unchecked")
	private List<ActionException> parseExceptionMaps(Element exceptionMaps) throws ClassNotFoundException
	{
		List<ActionException> exceptions = new ArrayList<ActionException>();
		List<Element> maps = exceptionMaps.elements(EXCEPTION_MAP_KEY);
		
		for(Element map : maps)
		{
			ActionException ae = new ActionException();
			
			String clazz	= map.attributeValue(EXCEPTION_KEY);
			ae.result		= map.attributeValue(EXCEPTION_RESULT_KEY);			
			
			if(clazz == null)
				ae.exception	= Exception.class;
			else
				ae.exception	= (Class<? extends Exception>)Class.forName(clazz);
			
			if(ae.result == null)
				ae.result = Action.EXCEPTION;
			
			exceptions.add(ae);
		}
		
		return exceptions.isEmpty() ? null : exceptions;
	}

	@SuppressWarnings("unchecked")
	private void parseInclude(Element root) throws ServletException
	{
		List<Element> includes = root.elements(INCLUDE_KEY);
		for(Element inc : includes)
		{
			String fileName	= inc.attributeValue(INCLUDE_FILE_KEY);
			String incFile	= GeneralHelper.getClassResourcePath(ActionDispatcher.class, fileName);
			loadConfig(incFile, false);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseActionPackage(Element root) throws ClassNotFoundException
	{
		List<Element> acsList = root.elements(ACTIONS_KEY);
		for(Element actions : acsList)
		{
			String path = actions.attributeValue(ACTIONS_PATH_KEY);
			
			if(GeneralHelper.isStrEmpty(path))
				path = ACTIONS_PATH_SEPARATOR;
			if(!path.startsWith(ACTIONS_PATH_SEPARATOR))
				path = ACTIONS_PATH_SEPARATOR + path;
			if(!path.endsWith(ACTIONS_PATH_SEPARATOR))
				path = path + ACTIONS_PATH_SEPARATOR;
			
			Map<String, ActionConfig> actionMap = actionPkgMap.get(path);
			
			if(actionMap == null)
			{
				actionMap = new HashMap<String, ActionConfig>();
				actionPkgMap.put(path, actionMap);
			}
			
			parseAction(actions, actionMap);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseAction(Element actions, Map<String, ActionConfig> actionMap) throws ClassNotFoundException
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
			ac.exceptions	= parseExceptionMaps(action);
			
			actionMap.put(ac.name, ac);
		}
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, ActionResult> parseResults(Element rsElement)
	{
		Map<String, ActionResult> map = new HashMap<String, ActionResult>();
		List<Element> results = rsElement.elements(RESULT_KEY);
		
		for(Element result : results)
		{
			ActionResult as = new ActionResult();
			
			as.name		= result.attributeValue(RESULT_NAME_KEY);
			String type	= result.attributeValue(RESULT_TYPE_KEY);
			as.url		= result.getTextTrim();
			
			if(as.name == null)
				as.name = Action.SUCCESS;
			
			if(type == null)
			{
				if(!as.name.equals(Action.NONE))
					as.type	= ActionResult.Type.DISPATCH;
				else
					as.type = ActionResult.Type.FINISH;
			}
			else
				as.type	= ActionResult.Type.valueOf(type.toUpperCase());
			
			map.put(as.name, as);
		}
		
		return map.isEmpty() ? null : map;
	}
	
	private void loadActionFilters() throws ServletException
	{
		if(!filterInfoList.isEmpty())
		{
			try
			{
				Map<Class<ActionFilter>, ActionFilter> filterMap = new HashMap<Class<ActionFilter>, ActionFilter>();
				
				cycleLoadActionFilterCache(filterMap);
        		initializeActionFilters(filterMap);
			}
			catch(Exception e)
			{
				throw new ServletException("init Action Filters fail", e);
			}
		}
	}

	private void cycleLoadActionFilterCache(Map<Class<ActionFilter>, ActionFilter> filterMap) throws InstantiationException, IllegalAccessException
	{
		Collection<Map<String, ActionConfig>> packages	= actionPkgMap.values();
		for(Map<String, ActionConfig> pkg : packages)
		{
			Collection<ActionConfig> confs = pkg.values();
			for(ActionConfig conf : confs)
				loadActionFilterCache(conf.acClass, filterMap);
		}
	}

	private void loadActionFilterCache(Class<? extends Action> clazz, Map<Class<ActionFilter>, ActionFilter> filterMap) throws InstantiationException, IllegalAccessException
	{
		LinkedList<ActionFilter> filters = filterCache.get(clazz);
		
		if(filters == null)
		{
			filters = matchActionFilters(clazz, filterMap);
			putActionFiltersInCache(clazz, filters);
		}
	}

	private LinkedList<ActionFilter> matchActionFilters(Class<? extends Action> clazz, Map<Class<ActionFilter>, ActionFilter> filterMap) throws InstantiationException, IllegalAccessException
	{
		LinkedList<ActionFilter> filters = new LinkedList<ActionFilter>();
		
		for(ActionFilterInfo info : filterInfoList)
		{
			Matcher m = info.pattern.matcher(clazz.getName());
			if(m.matches())
			{
				ActionFilter filter	= filterMap.get(info.afClass);
				if(filter == null)
				{
					filter = info.afClass.newInstance();				
					filterMap.put(info.afClass, filter);
				}
				
				filters.add(filter);
			}
		}
		
		return filters;
	}

	private void putActionFiltersInCache(Class<? extends Action> clazz, LinkedList<ActionFilter> filters)
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
    		
    		filterCache.put(clazz, filters);
		}
	}

	private void initializeActionFilters(Map<Class<ActionFilter>, ActionFilter> filterMap)
	{
		Map<Class<ActionFilter>, ActionFilter> tmpFilterMap = new HashMap<Class<ActionFilter>, ActionFilter>();
		
		for(ActionFilterInfo info : filterInfoList)
		{
			ActionFilter filter = tmpFilterMap.get(info.afClass);
			if(filter == null)
			{
				filter = filterMap.get(info.afClass);
				if(filter != null)
				{
					tmpFilterMap.put(info.afClass, filter);
					filterList.addLast(filter);
					filter.init();
				}
			}
		}
	}

	public void destroy()
	{
		while(!filterList.isEmpty())
			filterList.removeLast().destroy();
		
		filterList			= null;
		filterInfoList		= null;
		filterCache			= null;
		actionPkgMap		= null;
		globalResults		= null;
		globalExceptions	= null;
		
		/* 释放 HttpHelper 的 Servlet Context */
		HttpHelper.unInitializeServletContext();
	}

	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request		= (HttpServletRequest)req;
		HttpServletResponse response	= (HttpServletResponse)rep;
		
		if(encoding != null)
		{
    		request.setCharacterEncoding(encoding);
    		response.setCharacterEncoding(encoding);
		}
		
		String reqPath = request.getServletPath();
		
		if(reqPath != null && reqPath.endsWith(actionStuff))
		{
			String actionPath = reqPath.substring(0, reqPath.length() - actionStuff.length());
			dispatchAction(request, response, chain, new ActionPackage(actionPath));
		}
		else
			chain.doFilter(request, response);
	}

	private void dispatchAction(HttpServletRequest request, HttpServletResponse response, FilterChain chain, ActionPackage pkg) throws ServletException, IOException
	{
		ActionConfig ac = extractActionConfig(pkg);
		
		if(ac == null)
		{
			String msg = String.format("Action '%s' not found", pkg);
			response.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			return;
		}
		
		Action action = null;
		
		try
		{
			action = createAction(request, response, ac);
		}
		catch(Exception e)
		{
			String msg = String.format("Instantiate Action '%s (%s)' fail", pkg, ac.acClass.getName());
			throw new ServletException(msg, e);
		}
		
		try
		{
			ActionResult as	= null;
			String result	= executeAction(request, response, ac, action);
			
			if(ac.results != null)
				as = ac.results.get(result);
			if(as == null && globalResults != null)
				as = globalResults.get(result);
			if(as != null)
				processResult(request, response, chain, pkg, as, action);
			else
			{
				String msg = String.format("Result name '%s' in Action '%s' not found", result, pkg);
				throw new ServletException(msg);
			}
		}
		catch(Exception e)
		{	
			throw new ServletException(e);
		}
	}
	
	private ActionConfig extractActionConfig(ActionPackage pkg)
	{
		ActionConfig ac = null;
		Map<String, ActionConfig> actionMap = actionPkgMap.get(pkg.path);
        		
		if(actionMap != null)
			ac = actionMap.get(pkg.name);
		
		if(ac == null && !pkg.isDefaultPackage())
		{
			pkg.setDefaultPackage();
			return extractActionConfig(pkg);
		}
		
		return ac;
	}

	private String executeAction(HttpServletRequest request, HttpServletResponse response, ActionConfig ac, Action action) throws Exception
	{
		String result = null;
		LinkedList<ActionFilter> filters = filterCache.get(action.getClass());

		try
		{
			if(filters == null)
				result = ActionExecutor.execute(action);
			else
			{
				ActionExecutor executor = new ActionExecutor(filters, action, context, request, response);
				result = executor.invoke();
			}
		}
		catch(Exception e)
		{
			if(ac.exceptions != null)
				result = processActionException(request, ac.exceptions, action, e);
			if(result == null && globalExceptions != null)
				result = processActionException(request, globalExceptions, action, e);
			if(result == null)
				throw e;
		}
		
		return result;
	}
	
	private String processActionException(HttpServletRequest request, List<ActionException> aes, Action action, Exception e)
	{
		String result = null;

		for(ActionException ae : aes)
		{
			if(ae.exception.isAssignableFrom(e.getClass()))
			{
				request.setAttribute(REQ_ATTR_EXCEPTION, e);
				result = ae.result;
				
				break;
			}
		}
		
		return result;
	}

	private Action createAction(HttpServletRequest request, HttpServletResponse response, ActionConfig ac) throws InstantiationException, IllegalAccessException
	{
		Action action = ac.acClass.newInstance();
		
		if(action instanceof ActionContextAware)
		{
			ActionContextAware aw = (ActionContextAware)action;
			aw.setServletContext(context);
			aw.setRequest(request);
			aw.setResponse(response);
		}
		
		FormBeanParser.parse(action, request.getParameterMap());
		
		return action;
	}

	private void processResult(HttpServletRequest request, HttpServletResponse response, FilterChain chain, ActionPackage pkg, ActionResult as, Action action) throws ServletException, IOException
	{
		switch(as.type)
		{
		case DISPATCH:
			request.setAttribute(REQ_ATTR_ACTION, action);
			RequestDispatcher rd = request.getRequestDispatcher(as.url);
			
			if(rd != null)
				rd.forward(request, response);
			else
			{
				String msg = String.format("Dispatch URL '%s' not found", as.url);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, msg);
			}
			
			break;
		case REDIRECT:
			response.sendRedirect(as.url);
			break;
		case CHAIN:
			request.setAttribute(REQ_ATTR_ACTION, action);
			dispatchChainAction(request, response, chain, pkg, as.url);
			break;
		case FINISH:
			break;
		default:
			assert false;
		}
	}

	private void dispatchChainAction(HttpServletRequest request, HttpServletResponse response, FilterChain chain, ActionPackage currentPkg, String url) throws ServletException, IOException
	{
		ActionPackage pkg = new ActionPackage(url, currentPkg.path);
		dispatchAction(request, response, chain, pkg);
	}
	
	private static class ActionFilterInfo
	{
		Class<ActionFilter> afClass;
		Pattern pattern;
	}
	
	private static class ActionConfig
	{
		String name;
		Class<? extends Action> acClass;
		Map<String, ActionResult> results = new HashMap<String, ActionResult>();
		List<ActionException> exceptions;
	}
	
	private static class ActionResult
	{
		static enum Type
		{
			DISPATCH,	// 服务端重定向
			REDIRECT,	// 客户端重定向
			CHAIN,		// 传递到下一个 Action
			FINISH;		// 不转发
			
			@Override
			public String toString()
			{
				return super.toString().toLowerCase();
			}
		}

		String name;
		Type type;
		String url;
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
			int sepIndex = actionPath.lastIndexOf(ACTIONS_PATH_SEPARATOR);
			
			if(sepIndex != -1)
			{
				path = actionPath.substring(0, sepIndex + 1);
				if(currentPath != null && path.startsWith(ACTIONS_CURRENT_PATH_PREFIX))
					path = path.replace(ACTIONS_CURRENT_PATH_PREFIX, currentPath);
				if(!path.startsWith(ACTIONS_PATH_SEPARATOR))
					path = ACTIONS_PATH_SEPARATOR + path;
				
				if(sepIndex < actionPath.length() - 1)
					name = actionPath.substring(sepIndex + 1, actionPath.length());
			}
			else
			{
				path = ACTIONS_PATH_SEPARATOR;
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
		
		boolean isDefaultPackage()
		{
			return path.equals(ACTIONS_PATH_SEPARATOR);
		}
		
		void setDefaultPackage()
		{
			path = ACTIONS_PATH_SEPARATOR;
		}
	}
}
