/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version	: JessMA 3.2.1
 * Author	: Bruce Liang
 * Website	: http://www.jessma.org
 * Porject	: https://code.google.com/p/portal-basic
 * Bolg		: http://www.cnblogs.com/ldcsaa
 * WeiBo	: http://weibo.com/u/1402935851
 * QQ Group	: 75375912
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

package org.jessma.util;

import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * 日志记录器（用 log4j 实现）
 *
 */
public class Logger
{
	public enum Level
	{
		ALL,DEBUG,INFO,WARN,ERROR,FATAL,OFF,TRACE;
		
		public org.apache.log4j.Level toLog4jLevel()
		{
			return org.apache.log4j.Level.toLevel(name());
		}
	}
	
	/** 默认配置文件 */
	public static final	String DEFAULT_CONFIG_FILE_NAME = "log4j.properties";
	
	private static		Logger defaultLogger = new Logger();
	private				org.apache.log4j.Logger log4jLogger;
	
	/** 设置应用程序默认日志记录器名称 */
	public static final void setDefaultLoggerName(Class<?> clazz)
	{
		defaultLogger.log4jLogger = org.apache.log4j.Logger.getLogger(clazz);
	}
	
	/** 设置应用程序默认日志记录器名称 */
	public static final void setDefaultLoggerName(String name)
	{
		defaultLogger.log4jLogger = org.apache.log4j.Logger.getLogger(name);
	}
	
	/** 获取应用程序默认日志记录器对象 */
	public static final Logger getDefaultLogger()
	{
		return defaultLogger;
	}
	
	/** 初始化日志记录器 */
	public static final void initialize()
	{
		String file = GeneralHelper.getClassResourcePath(Logger.class, DEFAULT_CONFIG_FILE_NAME);
		initialize(file);
	}
	
	/** 初始化日志记录器 */
	public static final void initialize(String file)
	{
		PropertyConfigurator.configure(file);
	}
	
	/** 初始化日志记录器 */
	public static final void initialize(Properties properties)
	{
		PropertyConfigurator.configure(properties);
	}
	
	/** 关闭日志记录器 */
	public static final void shutdown()
	{
		LogManager.shutdown();
	}
	
	/** 创建日志记录器 */
	private Logger()
	{
	}
	
	/** 创建日志记录器 */
	public Logger(Class<?> clazz)
	{
		log4jLogger = org.apache.log4j.Logger.getLogger(clazz);
	}
	
	/** 创建日志记录器 */
	public Logger(String name)
	{
		log4jLogger = org.apache.log4j.Logger.getLogger(name);
	}
	
	/** 检查日志记录器是否可用 */
	public final boolean isValid()
	{
		return log4jLogger != null;
	}
	
	/** 记录 DEBUG 日志 */
	public final void debug(Object msg)
	{
		log4jLogger.debug(msg);
	}
	
	/** 记录 DEBUG 日志 */
	public final void debug(String format, Object ... args)
	{
		debug(String.format(format, args));
	}
	
	/** 记录 DEBUG 日志 */
	public final void debug(Object msg, Throwable e)
	{
		log4jLogger.debug(msg, e);
	}
	
	/** 记录 DEBUG 日志 */
	public final void debug(Throwable e, String format, Object ... args)
	{
		debug(String.format(format, args), e);
	}
	
	/** 记录 TRACE 日志 */
	public final void trace(Object msg)
	{
		log4jLogger.trace(msg);
	}
	
	/** 记录 TRACE 日志 */
	public final void trace(String format, Object ... args)
	{
		trace(String.format(format, args));
	}
	
	/** 记录 TRACE 日志 */
	public final void trace(Object msg, Throwable e)
	{
		log4jLogger.trace(msg, e);
	}
	
	/** 记录 TRACE 日志 */
	public final void trace(Throwable e, String format, Object ... args)
	{
		trace(String.format(format, args), e);
	}
	
	/** 记录 INFO 日志 */
	public final void info(Object msg)
	{
		log4jLogger.info(msg);
	}
	
	/** 记录 INFO 日志 */
	public final void info(String format, Object ... args)
	{
		info(String.format(format, args));
	}
	
	/** 记录 INFO 日志 */
	public final void info(Object msg, Throwable e)
	{
		log4jLogger.info(msg, e);
	}
	
	/** 记录 INFO 日志 */
	public final void info(Throwable e, String format, Object ... args)
	{
		info(String.format(format, args), e);
	}
	
	/** 记录 WARN 日志 */
	public final void warn(Object msg)
	{
		log4jLogger.warn(msg);
	}
	
	/** 记录 WARN 日志 */
	public final void warn(String format, Object ... args)
	{
		warn(String.format(format, args));
	}
	
	/** 记录 WARN 日志 */
	public final void warn(Object msg, Throwable e)
	{
		log4jLogger.warn(msg, e);
	}
	
	/** 记录 WARN 日志 */
	public final void warn(Throwable e, String format, Object ... args)
	{
		warn(String.format(format, args), e);
	}
	
	/** 记录 ERROR 日志 */
	public final void error(Object msg)
	{
		log4jLogger.error(msg);
	}
	
	/** 记录 ERROR 日志 */
	public final void error(String format, Object ... args)
	{
		error(String.format(format, args));
	}
	
	/** 记录 ERROR 日志 */
	public final void error(Object msg, Throwable e)
	{
		log4jLogger.error(msg, e);
	}
	
	/** 记录 ERROR 日志 */
	public final void error(Throwable e, String format, Object ... args)
	{
		error(String.format(format, args), e);
	}
	
	/** 记录 FATAL 日志 */
	public final void fatal(Object msg)
	{
		log4jLogger.fatal(msg);
	}
	
	/** 记录 FATAL 日志 */
	public final void fatal(String format, Object ... args)
	{
		fatal(String.format(format, args));
	}
	
	/** 记录 FATAL 日志 */
	public final void fatal(Object msg, Throwable e)
	{
		log4jLogger.fatal(msg, e);
	}
	
	/** 记录 FATAL 日志 */
	public final void fatal(Throwable e, String format, Object ... args)
	{
		fatal(String.format(format, args), e);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public final void log(Level level, Object msg)
	{
		log4jLogger.log(level.toLog4jLevel(), msg);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public final void log(Level level, String format, Object ... args)
	{
		log(level, String.format(format, args));
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public final void log(Level level, Object msg, Throwable e)
	{
		log4jLogger.log(level.toLog4jLevel(), msg, e);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public final void log(Throwable e, Level level, String format, Object ... args)
	{
		log(level, String.format(format, args), e);
	}
	
	/** 记录操作异常日志 */
	public final void exception(Throwable e, Object action, Level level,boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Execption occur on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(e.toString());
		String error = msg.toString();

		if(printStack)
			log(level, error, e);
		else
			log(level, error);
	}
	
	/** 记录操作失败日志 */
	public final void fail(Object action, Object module, boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Operation fail on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(module);

		error(msg.toString());
	}
	
	/** 记录服务器启动日志 */
	public final void logServerStartup(Object o)
	{
		info("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
		info("starting: %s ...", o);
	}
	
	/** 记录服务器关闭日志 */
	public final void logServerShutdown(Object o)
	{
		info("stoping: %s ...", o);
		info("<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-");
	}
}
