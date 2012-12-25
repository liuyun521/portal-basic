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

package com.bruce.mvc;

/**
 * 
 * {@link Action} 对象公共接口。
 *
 */
public interface Action
{
	/* ************************************ */
	/* ** 基本  {@link Action} Result Name ** */
	
	/** 预定义 {@link Action} Result Name */
	String SUCCESS		= "success";	// 成功
	/** 预定义 {@link Action} Result Name */
	String FAIL			= "fail";		// 失败
	/** 预定义 {@link Action} Result Name */
	String ERROR		= "error";		// 错误
	/** 预定义 {@link Action} Result Name */
	String EXCEPTION	= "exception";	// 异常
	/** 预定义 {@link Action} Result Name */
	String LOGIN		= "login";		// 登录
	/** 预定义 {@link Action} Result Name */
	String INPUT		= "input";		// 输入
	/** 预定义 {@link Action} Result Name */
	String ELSE			= "else";		// 否则
	/** 预定义 {@link Action} Result Name */
	String NONE			= "none";		// 没有
	
	/* ************************************ */
	/* ** 常用  {@link Action} Result Name ** */

	/** 预定义 {@link Action} Result Name */
	String ALL			= "all";		// 所有
	/** 预定义 {@link Action} Result Name */
	String BOTH			= "both";		// （两者）都
	/** 预定义 {@link Action} Result Name */
	String LOGOUT		= "logout";		// 登出
	/** 预定义 {@link Action} Result Name */
	String QUIT			= "quit";		// 退出
	/** 预定义 {@link Action} Result Name */
	String EXIT			= "exit";		// 退出
	/** 预定义 {@link Action} Result Name */
	String ACCEPT		= "accept";		// 接受
	/** 预定义 {@link Action} Result Name */
	String REFUSE		= "refuse";		// 拒绝
	/** 预定义 {@link Action} Result Name */
	String AUTHORIZE	= "authorize";	// 验证
	/** 预定义 {@link Action} Result Name */
	String ALLOW		= "allow";		// 允许
	/** 预定义 {@link Action} Result Name */
	String LOAD			= "load";		// 加载
	/** 预定义 {@link Action} Result Name */
	String RELOAD		= "reload";		// 重新加载
	/** 预定义 {@link Action} Result Name */
	String HIDE			= "hide";		// 隐藏
	/** 预定义 {@link Action} Result Name */
	String SHOW			= "show";		// 显示
	/** 预定义 {@link Action} Result Name */
	String DESTROY		= "destroy";	// 销毁
	/** 预定义 {@link Action} Result Name */
	String NEW			= "new";		// 创建
	/** 预定义 {@link Action} Result Name */
	String RENEW		= "renew";		// 恢复
	/** 预定义 {@link Action} Result Name */
	String EDIT			= "edit";		// 编辑
	/** 预定义 {@link Action} Result Name */
	String VERIFY		= "verify";		// 检验
	/** 预定义 {@link Action} Result Name */
	String CONFIRM		= "confirm";	// 确认
	/** 预定义 {@link Action} Result Name */
	String SUMMARY		= "summary";	// 摘要
	/** 预定义 {@link Action} Result Name */
	String DETAIL		= "detail";		// 细节
	/** 预定义 {@link Action} Result Name */
	String FORWARD		= "forward";	// 向前
	/** 预定义 {@link Action} Result Name */
	String BACK			= "back";		// 向后
	/** 预定义 {@link Action} Result Name */
	String VIEW			= "view";		// 视图
	/** 预定义 {@link Action} Result Name */
	String LIST			= "list";		// 列表
	/** 预定义 {@link Action} Result Name */
	String FIND			= "find";		// 查找
	/** 预定义 {@link Action} Result Name */
	String QUERY		= "query";		// 查询
	/** 预定义 {@link Action} Result Name */
	String SEARCH		= "search";		// 搜索
	/** 预定义 {@link Action} Result Name */
	String GET			= "get";		// 获取
	/** 预定义 {@link Action} Result Name */
	String SET			= "set";		// 设置
	/** 预定义 {@link Action} Result Name */
	String ADD			= "add";		// 添加
	/** 预定义 {@link Action} Result Name */
	String PUT			= "put";		// 赋予
	/** 预定义 {@link Action} Result Name */
	String CREATE		= "create";		// 更新
	/** 预定义 {@link Action} Result Name */
	String UPDATE		= "update";		// 更新
	/** 预定义 {@link Action} Result Name */
	String REFRESH		= "refresh";	// 刷新
	/** 预定义 {@link Action} Result Name */
	String DELETE		= "delete";		// 删除
	/** 预定义 {@link Action} Result Name */
	String ERASE		= "erase";		// 擦除
	/** 预定义 {@link Action} Result Name */
	String EMPTY		= "empty";		// 清空

	/* ************************************ */
	/* ** 保留  {@link Action} Result Name ** */

	/** 预定义 {@link Action} Result Name */
	String $0			= "$0";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $1			= "$1";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $2			= "$2";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $3			= "$3";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $4			= "$4";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $5			= "$5";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $6			= "$6";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $7			= "$7";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $8			= "$8";			// （保留）
	/** 预定义 {@link Action} Result Name */
	String $9			= "$9";			// （保留）

	/** 与 {@link Action} 相关的常量 */
	public static interface Constant
	{
		/** Request Attribute -> {@link Action} 对象 */
		String REQ_ATTR_ACTION		 = "__action";
		/** Request Attribute -> {@link Exception} 对象 */
		String REQ_ATTR_EXCEPTION	 = "__exception";
		/** Request Attribute -> HTTP 请求的 BASE URL */
		String REQ_ATTR_BASE_PATH	 = "__base";
		/** Application Attribute -> Servlet Context 的 ROOT PATH */
		String APP_ATTR_CONTEXT_PATH = "__context";
	}
	
	/** 与 {@link Action} 结果类型 */
	public static enum ResultType
	{
		/** 根据 Result Name 自动设置 */
		DEFAULT,
		/** 服务端重定向 */
		DISPATCH,
		/** 客户端重定向 */
		REDIRECT,
		/** 传递到下一个 Action */
		CHAIN,
		/** 不转发 */
		FINISH;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}
	
	/** {@link Action} 入口方法 */
	String execute() throws Exception;
}
