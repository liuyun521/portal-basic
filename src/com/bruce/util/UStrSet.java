package com.bruce.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * 
 * 所有值均为大写字母字符串的 {@link HashSet}
 *
 */
@SuppressWarnings("serial")
public class UStrSet extends HashSet<String>
{
	@Override
	public boolean add(String value)
	{
		return super.add(value.toUpperCase());
	}
	
	@Override
	public boolean remove(Object key)
	{
		key = key.toString().toUpperCase();
		return super.remove(key);
	}
	
	@Override
	public boolean contains(Object key)
	{
		key = key.toString().toUpperCase();
		return super.contains(key);
	}
	
	@Override
	public boolean retainAll(Collection<?> c)
	{
		ArrayList<String> list = new ArrayList<String>(c.size());
		
		for(Object o : c)
			list.add(o.toString().toUpperCase());

		return super.retainAll(list);
	}

}
