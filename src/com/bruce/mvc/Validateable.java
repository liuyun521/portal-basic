package com.bruce.mvc;

/** Action 输入验证接口 */
public interface Validateable
{
	/** 输入验证, 
	 * 
	 * 如果失败则不执行 {@link Action#execute()} 方法, 并立刻定向到 {@link Action#INPUT} 视图 
	 * 
	 */
	boolean validate();
}
