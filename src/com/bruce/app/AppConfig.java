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

package com.bruce.app;

import org.apache.log4j.Level;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import com.bruce.dao.SessionMgr;
import com.bruce.util.GeneralHelper;
import com.bruce.util.Logger;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * 
 * 系统配置类，保存程序系统配置。
 *
 */
@SuppressWarnings("rawtypes")
public class AppConfig
{
	private static Map<String, SessionMgr> sessionMgrs = new HashMap<String, SessionMgr>();
	private static UserConfigParser userCfgParser;
	private static AppLifeCycleListener appListener;
	
	/**
	 * 
	 * 获取所有 {@link SessionMgr} 
	 * 
	 * @return	  名称 -> 实例
	 * 
	 */
	public static final Map<String, SessionMgr> getSessionManagers()
	{
		return sessionMgrs;
	}
	
	/**
	 * 
	 * 获取指定名称的 {@link SessionMgr} 
	 * 
	 * @param name	: {@link SessionMgr} 名称
	 * @return	  	: {@link SessionMgr} 实例
	 * 
	 */
	public static final SessionMgr getSessionManager(String name)
	{
		return sessionMgrs.get(name);
	}

	static final void sendStartupNotice(ServletContext context, ServletContextEvent sce)
	{
		if(appListener != null)
			appListener.onStartup(context, sce);
	}
	
	static final void sendShutdownNotice(ServletContext context, ServletContextEvent sce)
	{
		if(appListener != null)
			appListener.onShutdown(context, sce);
	}

	@SuppressWarnings("unchecked")
	static final void initialize(String file)
	{
		try
		{
			SAXReader sr	= new SAXReader();
			Document doc	= sr.read(new File(file));
			Element root	= doc.getRootElement();
			
			Element sys = root.element("system");
			if(sys == null)
				throw new RuntimeException("'system' element not found");
			
			Element sms = sys.element("database-session-managers");
			if(sms == null)
				Logger.warn("'database-session-managers' element not found");
			else
			{
				// 注册数据库 Session 管理器
				List<Element> mgrs = sms.elements("manager");
				if(mgrs.size() == 0)
					Logger.warn("none of DATABASE SESSION MANAGER found");
				else
				{				
    				for(Element e : mgrs)
    				{
    					String name = e.attributeValue("name");
    					String clazz = e.attributeValue("class");
    					
    					if(GeneralHelper.isStrEmpty(name) || GeneralHelper.isStrEmpty(clazz))
    						throw new RuntimeException("manager's 'name' or 'class' attribute not valid");
    					
    					String[] params;
    					Element iniargs = e.element("initialize-args");
    					if(iniargs == null)
    						params = new String[0];
    					else
    					{
        					List<Element> args = iniargs.elements("arg");
        					params = new String[args.size()];
        					
        					for(int i = 0; i < args.size(); i++)
        						params[i] = args.get(i).getTextTrim();
     					}
    					
       					Logger.info(String.format("register DATABASE SESSION MANAGER '%s (%s)' ...", name, clazz));
       					
    					SessionMgr mgr = (SessionMgr)(Class.forName(clazz).newInstance());
    					mgr.initialize(params);
    					sessionMgrs.put(name, mgr);	
    				}
				}
			}
							
			// 注册 UserConfigParser
			Element parser = sys.element("user-config-parser");
			if(parser != null)
			{
				String clazz = parser.attributeValue("class");
				if(!GeneralHelper.isStrEmpty(clazz))
				{
					Logger.info(String.format("register USER CONFIG PARSER '%s' ...", clazz));
					userCfgParser = (UserConfigParser)(Class.forName(clazz).newInstance());
					
					// 解析用户配置信息
					Logger.info("parse user configuration ...");
					Element user = root.element("user");
					userCfgParser.parse(user);
				}
			}

			// 注册 AppLifeCycleListener
			Element listener = sys.element("app-life-cycle-listener");
			if(listener != null)
			{
				String clazz = listener.attributeValue("class");
				if(!GeneralHelper.isStrEmpty(clazz))
				{
					Logger.info(String.format("register APP LIFE CYCLE LISTENER '%s' ...", clazz));
					appListener = (AppLifeCycleListener)(Class.forName(clazz).newInstance());
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException("load application configuration fail", e);
		}
	}

	static void unInitialize()
	{		
		// 注销数据库 Session 管理器
		Set<Map.Entry<String, SessionMgr>> mgrs = sessionMgrs.entrySet();
		for(Map.Entry<String, SessionMgr> mgr : mgrs)
		{
			Logger.info(String.format("unregister DATABASE SESSION MANAGER '%s' ...", mgr.getKey()));
			mgr.getValue().unInitialize();
		}
		
		sessionMgrs.clear();
		
		// 卸载数据库驱动程序
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while(drivers.hasMoreElements())
		{
			Driver driver = drivers.nextElement();
			try
			{
				Logger.info(String.format("unregister JDBC DRIVER '%s' ...", driver));
				DriverManager.deregisterDriver(driver);
			}
			catch(SQLException e)
			{
				Logger.exception(e, String.format("unregister JDBC DRIVER '%s' fail", driver), Level.FATAL, true);
			}
		}
		
		userCfgParser	= null;
		appListener		= null;
	}
}
