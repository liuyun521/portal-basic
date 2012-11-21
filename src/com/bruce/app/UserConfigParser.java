package com.bruce.app;

import org.dom4j.Element;

/** 用户配置信息解析器接口 */
public interface UserConfigParser
{
	/** 
	 * 执行解析（从 '&lt;user&gt;' 节点开始）
	 * 
	 *  @param user	: app-config.xml 的 '&lt;user&gt;' 节点
	 */
	void parse(Element user);
}
