package test;

import com.bruce.util.cache.OSCacheWrapper;

public class TestOSCache
{
	private static final String GRP_1 = "G1";
	private static final String GRP_2 = "G2";
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
		
		OSCacheWrapper cache0 = new OSCacheWrapper();
		OSCacheWrapper cache1 = new OSCacheWrapper(GRP_1);
		OSCacheWrapper cache2 = new OSCacheWrapper(GRP_1, GRP_2);
		
		cache0.put(KEY_0, v0);
		cache1.put(KEY_1, v1);
		cache2.put(KEY_2, v2);
	}

	private static void getValue()
	{
		OSCacheWrapper cache = new OSCacheWrapper();
		
		String v0 = cache.get(KEY_0);
		Integer v1 = cache.get(KEY_1);
		String v2 = cache.get(KEY_2);
		
		System.out.printf("V0 = %s, V1 = %d, V2 = %s \r\n", v0, v1, v2);
		
		cache.flushGroup(GRP_2);
		
		v0 = cache.get(KEY_0);
		v1 = cache.get(KEY_1);
		v2 = cache.get(KEY_2);
		
		System.out.printf("V0 = %s, V1 = %d, V2 = %s \r\n", v0, v1, v2);
		
		cache.flushGroup(GRP_1);
		
		v0 = cache.get(KEY_0);
		v1 = cache.get(KEY_1);
		v2 = cache.get(KEY_2);
		
		System.out.printf("V0 = %s, V1 = %d, V2 = %s \r\n", v0, v1, v2);

		OSCacheWrapper.shutdown();
		
		cache = new OSCacheWrapper();
		
		v0 = cache.get(KEY_0);
		v1 = cache.get(KEY_1);
		v2 = cache.get(KEY_2);
		
		System.out.printf("V0 = %s, V1 = %d, V2 = %s \r\n", v0, v1, v2);
	}
}
