package com.bruce.app;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/** 应用程序生命周期监听器接口 */
public interface AppLifeCycleListener
{
	/**
	 *  程序启动通知
	 *  
	 *  @param context	: 应用程序的 {@link ServletContext}
	 *  @param sce	: 事件参数 {@link ServletContextEvent}
	 *  
	*/
	void onStartup(ServletContext context, ServletContextEvent sce);

	/**
	 *  程序关闭通知
	 *  
	 *  @param context	: 应用程序的 {@link ServletContext}
	 *  @param sce	: 事件参数 {@link ServletContextEvent}
	 *  
	*/
	void onShutdown(ServletContext context, ServletContextEvent sce);

}
