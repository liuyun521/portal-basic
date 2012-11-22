package com.bruce.util.cache;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.bruce.util.GeneralHelper;
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
