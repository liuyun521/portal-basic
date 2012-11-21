package com.bruce.util;

import java.util.HashMap;

/**
 * 
 * 所有键均为小写字母字符串的 {@link HashMap}
 *
 * @param <V>	: 任意类型
 */
@SuppressWarnings("serial")
public class LStrMap<V> extends HashMap<String, V>
{
	@Override
	public V put(String key, V value)
	{
		return super.put(key.toLowerCase(), value);
	}
	
	@Override
	public V get(Object key)
	{
		key = key.toString().toLowerCase();
		return super.get(key);
	}
	
	@Override
	public V remove(Object key)
	{
		key = key.toString().toLowerCase();
		return super.remove(key);
	}
	
	@Override
	public boolean containsKey(Object key)
	{
		key = key.toString().toLowerCase();
		return super.containsKey(key);
	}
}
