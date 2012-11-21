package com.bruce.util;

import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * 
 * 系统日志记录器
 *
 */
public class Logger
{
	private static		org.apache.log4j.Logger logger;
	
	/** 默认配置文件 */
	public static final	String DEFAULT_CONFIG_FILE_NAME = "log4j.properties";
	
	/** 获取记录对象 */
	public static final	org.apache.log4j.Logger getLogger()
	{
		return logger;
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
	
	/** 检查日志记录器是否已被初始化 */
	public static final boolean hasInitialized()
	{
		return logger != null;
	}
	
	/** 关闭日志记录器 */
	public static final void shutdown()
	{
		if(hasInitialized())
			LogManager.shutdown();
	}
	
	/** 设置日志记录器名称 */
	public static final void setAppLogger(Class<?> clazz)
	{
		logger = org.apache.log4j.Logger.getLogger(clazz);
	}
	
	/** 设置日志记录器名称 */
	public static final void setAppLogger(String name)
	{
		logger = org.apache.log4j.Logger.getLogger(name);
	}
	
	/** 记录 DEBUG 日志 */
	public static final void debug(Object msg)
	{
		logger.debug(msg);
	}
	
	/** 记录 DEBUG 日志 */
	public static final void debug(String format, Object ... args)
	{
		debug(String.format(format, args));
	}
	
	/** 记录 DEBUG 日志 */
	public static final void debug(Object msg, Throwable e)
	{
		logger.debug(msg, e);
	}
	
	/** 记录 DEBUG 日志 */
	public static final void debug(Throwable e, String format, Object ... args)
	{
		debug(String.format(format, args), e);
	}
	
	/** 记录 TRACE 日志 */
	public static final void trace(Object msg)
	{
		logger.trace(msg);
	}
	
	/** 记录 TRACE 日志 */
	public static final void trace(String format, Object ... args)
	{
		trace(String.format(format, args));
	}
	
	/** 记录 TRACE 日志 */
	public static final void trace(Object msg, Throwable e)
	{
		logger.trace(msg, e);
	}
	
	/** 记录 TRACE 日志 */
	public static final void trace(Throwable e, String format, Object ... args)
	{
		trace(String.format(format, args), e);
	}
	
	/** 记录 INFO 日志 */
	public static final void info(Object msg)
	{
		logger.info(msg);
	}
	
	/** 记录 INFO 日志 */
	public static final void info(String format, Object ... args)
	{
		info(String.format(format, args));
	}
	
	/** 记录 INFO 日志 */
	public static final void info(Object msg, Throwable e)
	{
		logger.info(msg, e);
	}
	
	/** 记录 INFO 日志 */
	public static final void info(Throwable e, String format, Object ... args)
	{
		info(String.format(format, args), e);
	}
	
	/** 记录 WARN 日志 */
	public static final void warn(Object msg)
	{
		logger.warn(msg);
	}
	
	/** 记录 WARN 日志 */
	public static final void warn(String format, Object ... args)
	{
		warn(String.format(format, args));
	}
	
	/** 记录 WARN 日志 */
	public static final void warn(Object msg, Throwable e)
	{
		logger.warn(msg, e);
	}
	
	/** 记录 WARN 日志 */
	public static final void warn(Throwable e, String format, Object ... args)
	{
		warn(String.format(format, args), e);
	}
	
	/** 记录 ERROR 日志 */
	public static final void error(Object msg)
	{
		logger.error(msg);
	}
	
	/** 记录 ERROR 日志 */
	public static final void error(String format, Object ... args)
	{
		error(String.format(format, args));
	}
	
	/** 记录 ERROR 日志 */
	public static final void error(Object msg, Throwable e)
	{
		logger.error(msg, e);
	}
	
	/** 记录 ERROR 日志 */
	public static final void error(Throwable e, String format, Object ... args)
	{
		error(String.format(format, args), e);
	}
	
	/** 记录 FATAL 日志 */
	public static final void fatal(Object msg)
	{
		logger.fatal(msg);
	}
	
	/** 记录 FATAL 日志 */
	public static final void fatal(String format, Object ... args)
	{
		fatal(String.format(format, args));
	}
	
	/** 记录 FATAL 日志 */
	public static final void fatal(Object msg, Throwable e)
	{
		logger.fatal(msg, e);
	}
	
	/** 记录 FATAL 日志 */
	public static final void fatal(Throwable e, String format, Object ... args)
	{
		fatal(String.format(format, args), e);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public static final void log(Level level, Object msg)
	{
		logger.log(level, msg);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public static final void fatal(Level level, String format, Object ... args)
	{
		log(level, String.format(format, args));
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public static final void log(Level level, Object msg, Throwable e)
	{
		logger.log(level, msg, e);
	}
	
	/** 记录 {@link Level} 级别的日志 */
	public static final void log(Throwable e, Level level, String format, Object ... args)
	{
		log(level, String.format(format, args), e);
	}
	
	/** 记录操作异常日志 */
	public static final void exception(Throwable e, Object action, Level level,boolean printStack)
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
	public static final void fail(Object action, Object module, boolean printStack)
	{
		StringBuilder msg = new StringBuilder("Operation fail on ");
		msg.append(action);
		msg.append(" --> ");
		msg.append(module);

		error(msg.toString());
	}
	
	/** 记录服务器启动日志 */
	public static final void logServerStartup(Object o)
	{
		info("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
		info("starting: %s ...", o);
	}
	
	/** 记录服务器关闭日志 */
	public static final void logServerShutdown(Object o)
	{
		info("stoping: %s ...", o);
		info("<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-<-");
	}
}
