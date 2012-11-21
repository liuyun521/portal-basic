package com.bruce.ext.spring;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.bruce.mvc.Action;
import com.bruce.mvc.ActionExecutor;
import com.bruce.mvc.ActionFilter;
import com.bruce.mvc.ActionSupport;
import com.bruce.util.BeanHelper;
import com.bruce.util.GeneralHelper;
import com.bruce.util.http.HttpHelper;

/** 解析 {@link SpringBean} 和 {@link SpringBeans} 的 {@link ActionFilter} */
public class SpringInjectFilter implements ActionFilter
{
	private WebApplicationContext context;
	private ContextLoaderListener listener;
	ServletContext servletContext = HttpHelper.getServletContext();
	private Map<Method, SpringAttr[]> springMap = new HashMap<Method, SpringAttr[]>();
	
	@Override
	public void init()
	{
		context = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		if(context == null)
		{
			listener = new ContextLoaderListener();
			listener.contextInitialized(new ServletContextEvent(servletContext));
			context  = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		}
	}

	@Override
	public void destroy()
	{
		if(listener != null)
			listener.contextDestroyed(new ServletContextEvent(servletContext));
	}

	@Override
	public String doFilter(ActionExecutor executor) throws Exception
	{
		Action action = executor.getAction();
		Method method = executor.getEntryMethod();

		checkSpringMap(executor, action, method);	
		tryInject(action, method);

		return executor.invoke();
	}

	private void checkSpringMap(ActionExecutor executor, Action action, Method method) throws Exception
	{
		if(!springMap.containsKey(method))
		{
			List<SpringAttr> springAttrList			= new ArrayList<SpringAttr>();
			Map<String, SpringBean> springBeanMap	= parseSpringBeans(executor, action, method);
			
			parseSpringAttrs(executor, action, springAttrList, springBeanMap);
			tryPutSpringMap(method, springAttrList);
		}
	}

	private void parseSpringAttrs(ActionExecutor executor, Action action, List<SpringAttr> springAttrList, Map<String, SpringBean> springBeanMap) throws Exception
	{
		Set<Entry<String, SpringBean>> entries = springBeanMap.entrySet();
		
		for(Entry<String, SpringBean> entry : entries)
			parseSpringAttr(executor, action, entry, springAttrList);
	}

	private void tryInject(Action action, Method method)
	{
		SpringAttr[] springAttrs = springMap.get(method);
		
		if(springAttrs != null)
		{
			for(SpringAttr springAttr : springAttrs)
				springAttr.inject(action);
		}
	}

	private void tryPutSpringMap(Method method, List<SpringAttr> springAttrList)
	{
		synchronized(springMap)
		{
			if(!springMap.containsKey(method))
			{
				SpringAttr[] springAttrs = springAttrList.isEmpty() ? null : springAttrList.toArray(new SpringAttr[springAttrList.size()]);
				
				springMap.put(method, springAttrs);
			}
		}
	}

	private void parseSpringAttr(ActionExecutor executor, Action action, Entry<String, SpringBean> entry, List<SpringAttr> springAttrList) throws Exception
	{
		String attr				= entry.getKey();
		SpringBean springBean	= entry.getValue();
		
		if(attr == null)
			return;		
		
		Class<? extends Action> actionClass = action.getClass();
		Class<?> stopClass = ActionSupport.class.isAssignableFrom(actionClass) ?
							 ActionSupport.class : Object.class;
		
		SpringAttr springAttr = new SpringAttr(attr);
		
		parseMode(executor, springBean, springAttr);
		parsePropertyOrField(executor, actionClass, stopClass, springAttr);

		springAttrList.add(springAttr);
	}

	private Map<String, SpringBean> parseSpringBeans(ActionExecutor executor, Action action, Method method) throws Exception
	{
		Map<String, SpringBean> springBeanMap	= new HashMap<String, SpringBean>();
		
		analysisSpringBeans(executor, action, method, springBeanMap);
		
		if(springBeanMap.isEmpty())
			analysisSpringBeans(executor, action, action.getClass(), springBeanMap);
		
		return springBeanMap;
	}
	
	private void parseMode(ActionExecutor executor, SpringBean springBean, SpringAttr springAttr)
	{
		String name		= springBean.name();
		Class<?> type	= springBean.type();
		
		if(GeneralHelper.isStrNotEmpty(name) && type != Object.class)
		{
			springAttr.name	= name;
			springAttr.type	= type;
			springAttr.mode	= Mode.BOTH;
		}
		else if(GeneralHelper.isStrNotEmpty(name))
		{
			springAttr.name	= name;
			springAttr.mode	= Mode.NAME;
			
		}
		else if(type != Object.class)
		{
			springAttr.type	= type;
			springAttr.mode	= Mode.TYPE;
			
		}
		else
		{
			springAttr.name	= springAttr.attr;
			springAttr.mode	= Mode.NAME;			
		}
	}

	private void parsePropertyOrField(ActionExecutor executor, Class<? extends Action> actionClass, Class<?> stopClass, SpringAttr springAttr) throws Exception
	{
		PropertyDescriptor pd	= BeanHelper.getPropDescByName(actionClass, stopClass, springAttr.attr);
		Method setter			= BeanHelper.getPropertyWriteMethod(pd);
		
		if(setter != null)
				springAttr.property = pd;
		else
		{							
			Field field = BeanHelper.getInstanceFiledByName(actionClass, stopClass, springAttr.attr);
			
			if(field != null)
					springAttr.field = field;
		}
		
		if(springAttr.property == null && springAttr.field == null)
			throwParseException(executor, null, String.format("no property or field named '%s'", springAttr.attr));
	}

	private void putSpringBeanMap(SpringBean springBean, Map<String, SpringBean> springBeanMap)
	{
		String name = springBean.value();
		
		if(!springBeanMap.containsKey(name))
			springBeanMap.put(name, springBean);			
	}

	private void analysisSpringBeans(ActionExecutor executor, Action action, AnnotatedElement element, Map<String, SpringBean> springBeanMap) throws Exception
	{
		SpringBean springBean = element.getAnnotation(SpringBean.class);
		
		if(springBean != null)
			putSpringBeanMap(springBean, springBeanMap);
		
		SpringBeans springBeans = element.getAnnotation(SpringBeans.class);
		
		if(springBeans != null)
		{
			SpringBean[] springBeanArr = springBeans.value();
			
			if(springBeanArr.length == 0)
				springBeanMap.put(null, null);
			else
			{
				for(SpringBean springBean2 : springBeanArr)
					putSpringBeanMap(springBean2, springBeanMap);
			}
		}
	}
	
	private static final void throwParseException(ActionExecutor executor, String name, String cause) throws Exception
	{
		String msg;
		if(GeneralHelper.isStrNotEmpty(name))
			msg =	String.format("Parse @SpringBean / @SpringBeans fail '%s#%s()' ['%s'] -> %s", 
					executor.getAction().getClass().getName(),
					executor.getEntryMethod().getName(), name, cause);
		else
			msg =	String.format("Parse @SpringBean / @SpringBeans fail '%s#%s()' -> %s", 
    				executor.getAction().getClass().getName(),
    				executor.getEntryMethod().getName(), cause);
			
		
		throw new RuntimeException(msg);
	}
	
	private static enum Mode
	{
		NAME,
		TYPE,
		BOTH;
	}

	private class SpringAttr
	{
		String attr;
		PropertyDescriptor property;
		Field field;
		String name;
		Class<?> type;
		Mode mode;
		
		public SpringAttr(String attr)
		{
			this.attr = attr;
		}
		
		private boolean inject(Action action)
		{
			Object bean = null;
			
			switch(mode)
			{
			case NAME:
				bean = context.getBean(name);
				break;
			case TYPE:
				bean = context.getBean(type);
				break;
			case BOTH:
				bean = context.getBean(name, type);
				break;
			default:
				assert false;
			}
			
			if(property != null)
				return BeanHelper.setProperty(action, property, bean);
			else if(field != null)
				return BeanHelper.setFieldValue(action, field, bean);
			
			return false;
		}
	}
}
