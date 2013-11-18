/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version	: JessMA 3.3.1
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

package org.jessma.util.cache;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jessma.util.GeneralHelper;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * 
 * OSCache 缓存帮助类（简化 OSCache 缓存操作）
 *
 */
public class OSCacheWrapper
{
	// 默认过期时间(单位为秒);
	private int defaultRefreshPeriod;
	// 默认组
	private String[]	 defaultGroups;
	// 唯一 CacheAdmin 实例
	private static GeneralCacheAdministrator instance;

	public OSCacheWrapper()
	{
		this(CacheEntry.INDEFINITE_EXPIRY);
	}

	public OSCacheWrapper(String ... defaultGroups)
	{
		this(CacheEntry.INDEFINITE_EXPIRY, defaultGroups);
	}
	
	public OSCacheWrapper(int defaultRefreshPeriod)
	{
		this(defaultRefreshPeriod, new String[]{});
	}

	public OSCacheWrapper(int defaultRefreshPeriod, String ... defaultGroups)
	{
		this.defaultRefreshPeriod = defaultRefreshPeriod;
		
		if(defaultGroups.length > 0)
		{
			Set<String> groups = new HashSet<String>();
			for(String group : defaultGroups)
			{
				if(GeneralHelper.isStrNotEmpty(group))
					groups.add(group);
			}
			
			if(!groups.isEmpty())
			{
				this.defaultGroups	= new String[groups.size()];
				groups.toArray(this.defaultGroups);
			}
			else
				this.defaultGroups =  null;
		}
		else
			this.defaultGroups =  null;

		if(instance == null)
			createCacheAdminInstance();
	}

	private static GeneralCacheAdministrator createCacheAdminInstance()
	{
		if(instance == null)
		{
			synchronized(OSCacheWrapper.class)
			{
				if(instance == null)
					instance = new GeneralCacheAdministrator();
			}
		}

		return instance;
	}
	
	public <T> void put(String key, T content)
	{
		getCache().putInCache(key, content, defaultGroups, null, null);
	}

	public <T> void put(String key, T content, EntryRefreshPolicy policy)
	{
		getCache().putInCache(key, content, defaultGroups, policy, null);
	}

	public <T> void put(String key, T content, String ... groups)
	{
		if(groups.length == 0)
			groups = null;
		
		getCache().putInCache(key, content, groups, null, null);
	}

	public <T> void put(String key, T content, EntryRefreshPolicy policy, String ... groups)
	{
		if(groups.length == 0)
			groups = null;
		
		getCache().putInCache(key, content, groups, policy, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key)
	{
		Object obj = null;
		
		try
		{
			obj = getCache().getFromCache(key, defaultRefreshPeriod);
		}
		catch(NeedsRefreshException e)
		{
			cancelUpdate(key);
		}
		
		return (T)obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, int refreshPeriod)
	{
		Object obj = null;
		
		try
		{
			obj = getCache().getFromCache(key, refreshPeriod);
		}
		catch(NeedsRefreshException e)
		{
			cancelUpdate(key);
		}
		
		return (T)obj;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, int refreshPeriod, String cronExpression)
	{
		Object obj = null;
		
		try
		{
			obj = getCache().getFromCache(key, refreshPeriod, cronExpression);
		}
		catch(NeedsRefreshException e)
		{
			cancelUpdate(key);
		}
		
		return (T)obj;
	}
	
	public GeneralCacheAdministrator getAdministrator()
	{
		return instance;
	}
	
	public Cache getCache()
	{
		return instance.getCache();
	}
	
	public void cancelUpdate(String key)
	{
		getCache().cancelUpdate(key);
	}

	public void remove(String key)
	{
		getCache().removeEntry(key);
	}

	public void flushAll()
	{
		instance.flushAll();
	}

	public void flushAll(Date date)
	{
		getCache().flushAll(date);
	}
	
	public void flushAll(Date date, String origin)
	{
		getCache().flushAll(date, origin);
	}

	public void flushEntry(String key)
	{
		getCache().flushEntry(key);
	}

	public void flushEntry(String key, String origin)
	{
		getCache().flushEntry(key, origin);
	}
	
	public void flushGroup()
	{
		if(defaultGroups != null)
		{
			for(String group : defaultGroups)
				getCache().flushGroup(group);
		}
	}
	
	public void flushGroup(String group)
	{
		getCache().flushGroup(group);
	}
	
	public void flushGroup(String group, String origin)
	{
		getCache().flushGroup(group, origin);
	}
	
	public static void shutdown()
	{
		synchronized(OSCacheWrapper.class)
		{
			instance.destroy();
			instance = null;		
		}
	}
}
