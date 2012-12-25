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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Level;

import com.bruce.util.GeneralHelper;
import com.bruce.util.Logger;

/**
 * 
 * 应用程序启动监听器，实现 {@link ServletContextListener} 接口。
 *
 */
public class AppListener implements ServletContextListener
{
	private static final String LOGGER_NAME					= "Portal";
	private static final String DEFAULT_APP_CONFIG_FILE		= "app-config.xml";
	private static final String APP_CONFIG_FILE_KEY			= "app-config-file";
	private static final String LOGGER_CONFIG_FILE_KEY		= "logger-config-file";
	
	private ServletContext context;
		
	public void contextInitialized(ServletContextEvent sce)
	{
		context = sce.getServletContext();
		
		String appConfigFile = context.getInitParameter(APP_CONFIG_FILE_KEY);
		if(GeneralHelper.isStrEmpty(appConfigFile))
			appConfigFile = DEFAULT_APP_CONFIG_FILE;

		String loggerConfigFile = context.getInitParameter(LOGGER_CONFIG_FILE_KEY);
		if(GeneralHelper.isStrEmpty(loggerConfigFile))
			loggerConfigFile = Logger.DEFAULT_CONFIG_FILE_NAME;
		
		appConfigFile		= GeneralHelper.getClassResourcePath(AppListener.class, appConfigFile);
		loggerConfigFile	= GeneralHelper.getClassResourcePath(AppListener.class, loggerConfigFile);

		try
		{
			/* 启动日志组件 */
			Logger.initialize(loggerConfigFile);
			Logger.setAppLogger(LOGGER_NAME);
			assert Logger.hasInitialized();

			Logger.logServerStartup(this);
			
			/* 加载程序配置 */
			Logger.info("load application configuration ...");
			AppConfig.initialize(appConfigFile);
			
			/* 发送程序启动通知 */
			AppConfig.sendStartupNotice(context, sce);
		}
		catch(Exception e)
		{
			String msg = "application startup fail";
			
			if(Logger.hasInitialized())
				Logger.exception(e, msg, Level.FATAL, true);

			throw new RuntimeException(msg, e);
		}
	}
	
	public void contextDestroyed(ServletContextEvent sce)
	{
		try
		{
    		/* 发送程序停止通知 */
    		AppConfig.sendShutdownNotice(context, sce);	
    		
    		/* 卸载程序资源 */
    		AppConfig.unInitialize();
    		
    		/* 关闭日志组件 */
    		Logger.logServerShutdown(this);
    		Logger.shutdown();
   		}
		catch(Exception e)
		{
			String msg = "application shutdown exception";
			
			if(Logger.hasInitialized())
				Logger.exception(e, msg, Level.FATAL, true);

			throw new RuntimeException(msg, e);

		}
	}
}
