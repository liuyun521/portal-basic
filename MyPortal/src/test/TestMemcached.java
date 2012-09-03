package test;

import java.util.Date;

import com.bruce.util.cache.MemCachedPool;
import com.danga.MemCached.MemCachedClient;

public class TestMemcached
{
	public static final String POOL_NAME = "my-pool";
	
	public static void main(String[] args)
	{
		createCache();
		doTest();
		shutdownCache();
	}

	private static void createCache()
	{
		// 获取指定名称的 Cache Pool
		MemCachedPool pool = MemCachedPool.getInstance(POOL_NAME);
		
		// 初始化检查
		if(!pool.hasInitialized())
		{
			// 设置 Pool 属性
			pool.setTcp(true);
			pool.setAliveCheck(true);
			pool.setBinaryProtocal(true);
			pool.setServers("192.168.1.142:11211", "192.168.1.142:11212");
			pool.setWeights(1, 2, 3);
			
			// 初始化 Pool
			pool.initialize();
		}
	}

	private static void doTest()
	{
		// 获取 Pool 内置的 Cache Client 对象
		MemCachedClient client = MemCachedPool.getCachedClient(POOL_NAME);
		// （或者） 创建 Pool 的 Cache Client 对象
		//MemCachedClient client2 = MemCachedPool.createCachedClient(POOL_NAME, false);
		
		// 利用 Cache Client 对象存储数据
		client.set("name", "怪兽", (Date)null);
		client.set("age", 33);
		// 利用 Cache Client 对象获取数据
		String name = (String)client.get("name");
		Integer age = (Integer)client.get("age");
		
		System.out.printf("name = %s, age = %d \r\n", name, age);
	}

	private static void shutdownCache()
	{
		// 销毁 Pool
		MemCachedPool.shutdown();
	}
}
