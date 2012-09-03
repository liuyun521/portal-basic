package com.bruce.mvc;

/**
 * 
 * {@link Action} 对象公共接口。
 *
 */
public interface Action
{
	/** 预定义 {@link Action} 结果 */
	String SUCCESS		= "success";
	/** 预定义 {@link Action} 结果 */
	String FAIL			= "fail";
	/** 预定义 {@link Action} 结果 */
	String ERROR		= "error";
	/** 预定义 {@link Action} 结果 */
	String EXCEPTION	= "exception";
	/** 预定义 {@link Action} 结果 */
	String LOGIN		= "login";
	/** 预定义 {@link Action} 结果 */
	String INPUT		= "input";
	/** 预定义 {@link Action} 结果 */
	String ELSE			= "else";
	/** 预定义 {@link Action} 结果 */
	String NONE			= "none";
	/** 预定义 {@link Action} 结果 */
	String $0			= "$0";
	/** 预定义 {@link Action} 结果 */
	String $1			= "$1";
	/** 预定义 {@link Action} 结果 */
	String $2			= "$2";
	/** 预定义 {@link Action} 结果 */
	String $3			= "$3";
	/** 预定义 {@link Action} 结果 */
	String $4			= "$4";
	/** 预定义 {@link Action} 结果 */
	String $5			= "$5";
	/** 预定义 {@link Action} 结果 */
	String $6			= "$6";
	/** 预定义 {@link Action} 结果 */
	String $7			= "$7";
	/** 预定义 {@link Action} 结果 */
	String $8			= "$8";
	/** 预定义 {@link Action} 结果 */
	String $9			= "$9";

	
	/** {@link Action} 入口方法 */
	String execute() throws Exception;
}
