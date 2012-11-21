package com.bruce.mvc;

/** {@link Action} 过滤器接口
 * 
 * 拦截 {@link Action} 的 execute() 方法，进行前置或后置处理。
 * 也可以进行短路处理，屏蔽 execute() 方法
 *  
*/
public interface ActionFilter
{
	/** 初始化方法，程序启动时调用 */
	void init();
	/** 清理方法，程序关闭时调用 */
	void destroy();
	/** 拦截方法 */
	String doFilter(ActionExecutor executor) throws Exception;
}
