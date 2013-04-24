/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version	: 3.1.1
 * Author	: Bruce Liang
 * Porject	: https://code.google.com/p/portal-basic
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

package com.bruce.dao.jdbc;

import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.logicalcobwebs.proxool.configuration.PropertyConfigurator;

import com.bruce.util.GeneralHelper;

/**
 * 
 * Proxool Session 管理器
 *
 */
public class ProxoolSessionMgr extends AbstractJdbcSessionMgr
{
	/** Proxool 连接池名称前缀 */
	public static final String PROXOOL_PREFIX			= "proxool.";
	/** Proxool 默认配置文件 */
	public static final String DEFAULT_CONFIG_FILE		= PROXOOL_PREFIX + "xml";
	/** Proxool 默认连接池名称 */
	public static final String DEFAULT_CONNECTION_ID	= PROXOOL_PREFIX + "portal";

	private String connectionId;
	
	/** 
	 * 初始化 
	 * 
	 * @param args <br>
	 *			[0]	: connectionId （默认：{@link ProxoolSessionMgr#DEFAULT_CONNECTION_ID}） <br>
	 * 			[1]	: configFile （默认：{@link ProxoolSessionMgr#DEFAULT_CONFIG_FILE}） <br>
	 * 			[2]	: isXml （默认：true） <br>
	 * @throws InvalidParameterException
	 * @throws JdbcException
	 * 
	*/
	@Override
	public void initialize(String ... args)
	{
		if(args.length == 0)
			initialize();
		else if(args.length == 1)
			initialize(args[0]);
		else if(args.length == 2)
			initialize(args[0], args[1]);
		else if(args.length == 3)
		{
			boolean isXml = !"false".equalsIgnoreCase(args[2]);
			initialize(args[0], args[1], isXml);
		}
		else
			throw new InvalidParameterException("ProxoolSessionMgr initialize fail (invalid paramers)");
	}
	
	/** 初始化 */
	public void initialize()
	{
		initialize((String)null);
	}
	
	/** 初始化 */
	public void initialize(String connectionId)
	{
		initialize(connectionId, null);
	}
	
	/** 初始化 */
	public void initialize(String connectionId, String configFile)
	{
		initialize(connectionId, configFile, true);
	}
	
	/** 初始化 */
	public void initialize(String connectionId, String configFile, boolean isXml)
	{
		try
		{
			if(connectionId != null)
			{
				if(!connectionId.startsWith(PROXOOL_PREFIX))
					connectionId = PROXOOL_PREFIX + connectionId;
				
				this.connectionId = connectionId;
			}
			else
				this.connectionId = DEFAULT_CONNECTION_ID;
			
			this.configFile = GeneralHelper.getClassResourcePath(	ProxoolSessionMgr.class,
																	GeneralHelper.isStrNotEmpty(configFile) ?
																	configFile : DEFAULT_CONFIG_FILE);
        		
    		if(isXml)
    			JAXPConfigurator.configure(this.configFile, false);
    		else
    			 PropertyConfigurator.configure(this.configFile);
    		
    		loadDefalutTransIsoLevel();
		}
		catch(Exception e)
		{
			try {unInitialize();} catch(Exception e2) {}
			throw new JdbcException(e);
		}
	}
	
	/** 注销 */
	@Override
	public void unInitialize()
	{
		try
		{
			int length = ProxoolFacade.getAliases().length;
			
			if(length == 1)
				ProxoolFacade.shutdown(0);
			else if(length > 1)
			{
				String pool = connectionId.replaceFirst(PROXOOL_PREFIX, "");
				ProxoolFacade.removeConnectionPool(pool);
			}
		}
		catch(ProxoolException e)
		{
			throw new JdbcException(String.format("ProxoolSessionMgr uninitialize fail (%s)", e));
		}
	}
	
	/** 获取数据库连接对象 
	 * @throws SQLException */
	@Override
	protected Connection getInternalConnection() throws SQLException
	{
		return DriverManager.getConnection(connectionId);
	}
}
