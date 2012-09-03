package test;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;

import com.bruce.util.cache.EHCacheWrapper;

@SuppressWarnings("unused")
public class TestEHCache
{
	private static final String MY_CACHE_0 = "mycache_0";
	private static final String MY_CACHE_1 = "mycache";
	private static final String MY_CACHE_2 = "mycache_2";
	private static final String MY_CACHE_3 = "mycache_3";
	private static final String KEY_0 = "K0";
	private static final String KEY_1 = "K1";
	private static final String KEY_2 = "K2";
	
	public static void main(String[] args)
	{
		putValue();
		getValue();
	}

	private static void putValue()
	{
		String v0 = "DEFAULT";
		int v1 = 123;
		String v2 = "XYZ";
		
		CacheManager manager = CacheManager.create();
		
		EHCacheWrapper cw0 = EHCacheWrapper.addCacheIfAbsent(manager, MY_CACHE_0);
		EHCacheWrapper cw1 = EHCacheWrapper.getCache(manager, MY_CACHE_1);
		EHCacheWrapper cw2 = EHCacheWrapper.addCacheIfAbsent(manager, MY_CACHE_0);
		
		EHCacheWrapper cw3 = EHCacheWrapper.getCache(manager, MY_CACHE_3);
		EHCacheWrapper.addCache(manager, MY_CACHE_3);
		cw3 = EHCacheWrapper.getCache(manager, MY_CACHE_3);

		cw0.put(KEY_0, v0);
		cw1.put(KEY_1, v1);
		cw2.put(KEY_2, v2);
	}

	private static void getValue()
	{
		CacheManager manager = CacheManager.create();
		
		EHCacheWrapper cw0 = EHCacheWrapper.getCache(manager, MY_CACHE_0);
		EHCacheWrapper cw1 = EHCacheWrapper.getCache(manager, MY_CACHE_1);
		EHCacheWrapper cw2 = EHCacheWrapper.addCacheIfAbsent(manager, MY_CACHE_0);

		
		String v0 = cw0.getValue(KEY_2);
		Integer v1 = cw1.getValue(KEY_1);
		String v2 = cw2.getValue(KEY_0);
		
		System.out.printf("V0 = %s, V1 = %d, V2 = %s \r\n", v0, v1, v2);
		
		EHCacheWrapper.removeCache(manager, MY_CACHE_1);
		
		v0 = cw0.getValue(KEY_2);
		Status st1 = cw1.getCache().getStatus();
		//v1 = null;
		v2 = cw2.getValue(KEY_0);
		
		System.out.printf("V0 = %s, ST1 = <%s>, V2 = %s \r\n", v0, st1, v2);
		
		//EHCacheWrapper.removeAllCaches(manager);
		EHCacheWrapper.shutdown(manager);
	}
}
