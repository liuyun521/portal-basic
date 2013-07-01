/*
 * Copyright Bruce Liang (ldcsaa@gmail.com)
 *
 * Version	: JessMA 3.2.1
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jessma.util.KV;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.loader.CacheLoader;

/**
 * 
 * EHCache 缓存帮助类（简化 EHCache 缓存操作）
 *
 */
public class EHCacheWrapper
{	
	private Cache cache;
	
	/** 参考：{@link CacheManager#addCache(String name)} */
	public static final void addCache(CacheManager manager, String name)
	{
		manager.addCache(name);
	}
	
	/** 参考：{@link CacheManager#addCacheIfAbsent(String name)} */
	public static final EHCacheWrapper addCacheIfAbsent(CacheManager manager, String name)
	{
		Ehcache cache = manager.addCacheIfAbsent(name);
		
		if(cache instanceof Cache)
			return new EHCacheWrapper((Cache)cache);
		
		return null;
	}
	
	/** 参考：{@link CacheManager#getCache(String name)} */
	public static final EHCacheWrapper getCache(CacheManager manager, String name)
	{
		Cache cache = manager.getCache(name);
		return cache != null ? new EHCacheWrapper(cache) : null;
	}
	
	/** 参考：{@link CacheManager#removalAll()} */
	public static final void removeAllCaches(CacheManager manager)
	{
		manager.removalAll();
	}
	
	/** 参考：{@link CacheManager#removeCache(String name)} */
	public static final void removeCache(CacheManager manager, String name)
	{
		manager.removeCache(name);
	}
	
	/** 参考：{@link CacheManager#shutdown()} */
	public static final void shutdown(CacheManager manager)
	{
		manager.shutdown();
	}
		
	public EHCacheWrapper(Cache cache)
	{
		this.cache = cache;
	}
	
	public Cache getCache()
	{
		return cache;
	}
	
	public CacheManager getCacheManager()
	{
		return cache.getCacheManager();
	}
	
	public void put(Element element)
	{
		cache.put(element);
	}
	
	public <K, V> void put(K key, V value)
	{
		put(new Element(key, value));
	}
	
	public void put(Element element, boolean doNotNotifyCacheReplicators)
	{
		cache.put(element, doNotNotifyCacheReplicators);
	}
	
	public <K, V> void put(K key, V value, boolean doNotNotifyCacheReplicators)
	{
		put(new Element(key, value), doNotNotifyCacheReplicators);
	}
	
	public void putAllElements(Collection<Element> elements)
	{
		cache.putAll(elements);
	}
	
	public <K, V> void putAllValues(Map<K, V> kvs)
	{
		int size = kvs.size();
		
		if(size > 0)
		{
			List<Element> list = new ArrayList<Element>(size);
			for(Map.Entry<K, V> e : kvs.entrySet())
				list.add(new Element(e.getKey(), e.getValue()));
			
			putAllElements(list);
		}
	}
	
	public <K, V> void putAllKVs(Collection<KV<K, V>> kvs)
	{
		int size = kvs.size();
		
		if(size > 0)
		{
			List<Element> list = new ArrayList<Element>(size);
			for(KV<K, V> e : kvs)
				list.add(new Element(e.getKey(), e.getValue()));
			
			putAllElements(list);
		}
	}
	
	public <K, V> void putAllKVs(KV<K, V> ... kvs)
	{
		List<KV<K, V>> list = Arrays.asList(kvs);
		putAllKVs(list);
	}

	public Element putIfAbsent(Element element)
	{
		return cache.putIfAbsent(element);
	}
	
	public <K, V> Element putIfAbsent(K key, V value)
	{
		return putIfAbsent(new Element(key, value));
	}
	
	public Element putIfAbsent(Element element, boolean doNotNotifyCacheReplicators)
	{
		return cache.putIfAbsent(element, doNotNotifyCacheReplicators);
	}
	
	public <K, V> Element putIfAbsent(K key, V value, boolean doNotNotifyCacheReplicators)
	{
		return putIfAbsent(new Element(key, value), doNotNotifyCacheReplicators);
	}

	public void putQuiet(Element element)
	{
		cache.putQuiet(element);
	}
	
	public <K, V> void putQuiet(K key, V value)
	{
		putQuiet(new Element(key, value));
	}
	
	public void putWithWriter(Element element)
	{
		cache.putWithWriter(element);
	}
	
	public <K, V> void putWithWriter(K key, V value)
	{
		putWithWriter(new Element(key, value));
	}
	
	public boolean remove(Object key)
	{
		return cache.remove(key);
	}

	public boolean remove(Serializable key)
	{
		return cache.remove(key);
	}
	
	public boolean remove(Object key, boolean doNotNotifyCacheReplicators)
	{
		return cache.remove(key, doNotNotifyCacheReplicators);
	}

	public boolean remove(Serializable key, boolean doNotNotifyCacheReplicators)
	{
		return cache.remove(key, doNotNotifyCacheReplicators);
	}

	public void removeAll()
	{
		cache.removeAll();
	}

	public void removeAll(boolean doNotNotifyCacheReplicators)
	{
		cache.removeAll(doNotNotifyCacheReplicators);
	}
	
	public void removeAll(Collection<?> keys)
	{
		cache.removeAll(keys);
	}
	
	public <K> void removeAll(K ... keys)
	{
		List<K> list = Arrays.asList(keys);
		removeAll(list);
	}
	
	public void removeAll(Collection<?> keys, boolean doNotNotifyCacheReplicators)
	{
		cache.removeAll(keys, doNotNotifyCacheReplicators);
	}
	
	public <K> void removeAll(boolean doNotNotifyCacheReplicators, K ... keys)
	{
		List<K> list = Arrays.asList(keys);
		removeAll(list, doNotNotifyCacheReplicators);
	}
	
	public Element removeAndReturnElement(Object key)
	{
		return cache.removeAndReturnElement(key);
	}
	
	public <V> V removeAndReturnValue(Object key)
	{
		Element e = removeAndReturnElement(key);
		return elementToValue(e);
	}
	
	public boolean removeElement(Element element)
	{
		return cache.removeElement(element);
	}
	
	public boolean removeQuiet(Object key)
	{
		return cache.removeQuiet(key);
	}

	public boolean removeQuiet(Serializable key)
	{
		return cache.removeQuiet(key);
	}
	
	public boolean removeWithWriter(Object key)
	{
		return cache.removeWithWriter(key);
	}

	public Element replace(Element element)
	{
		return cache.replace(element);
	}
	
	public <K, V> Element replace(K key, V value)
	{
		return replace(new Element(key, value));
	}
	
	public boolean replace(Element old, Element element)
	{
		return cache.replace(old, element);
	}
	
	public <K, V1, V2> boolean replace(K key, V1 old, V2 element)
	{
		return replace(new Element(key, old), new Element(key, element));
	}
	
	public Element getElement(Object key)
	{
		return cache.get(key);
	}

	public <V> V getValue(Object key)
	{
		Element e = getElement(key);
		return elementToValue(e);
	}
	
	public Element getElement(Serializable key)
	{
		return cache.get(key);
	}

	public <V> V getValue(Serializable key)
	{
		Element e = getElement(key);
		return elementToValue(e);
	}
	
	public Map<Object, Element> getAllElements(Collection<?> keys)
	{
		return cache.getAll(keys);
	}
	
	public Map<Object, Element> getAllElements(Object ... keys)
	{
		List<?> list = Arrays.asList(keys);
		return getAllElements(list);
	}
	
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getAllValues(Collection<?> keys)
	{
		Map<Object, Element> elements = getAllElements(keys);
		Map<K, V> map = new HashMap<K, V>(elements.size());
		
		for(Map.Entry<Object, Element> e : elements.entrySet())
		{
			Element ev	= e.getValue();
			K key		= (K)e.getKey();	
			V value		= elementToValue(ev);
			map.put(key, value);
		}
		
		return map;
	}
	
	public <K, V> Map<K, V> getAllValues(K ... keys)
	{
		List<?> list = Arrays.asList(keys);
		return getAllValues(list);
	}
	
	@SuppressWarnings("rawtypes")
	public Map getAllWithLoader(Collection keys, Object loaderArgument)
	{
		return cache.getAllWithLoader(keys, loaderArgument);
	}
	
	@SuppressWarnings("rawtypes")
	public Map getAllWithLoader(Object loaderArgument, Object ...keys)
	{
		List<?> list = Arrays.asList(keys);
		return getAllWithLoader(list, loaderArgument);
	}
	
	public Element getElementWithLoader(Object key, CacheLoader loader, Object loaderArgument)
	{
		return cache.getWithLoader(key, loader, loaderArgument);
	}

	public <V> V getValueWithLoader(Object key, CacheLoader loader, Object loaderArgument)
	{
		Element e = getElementWithLoader(key, loader, loaderArgument);
		return elementToValue(e);
	}

	public Element getElementQuiet(Object key)
	{
		return cache.getQuiet(key);
	}
	
	public <V> V getValueQuiet(Object key)
	{
		Element e = getElementQuiet(key);
		return elementToValue(e);
	}
	
	public Element getElementQuiet(Serializable key)
	{
		return cache.getQuiet(key);
	}
	
	public <V> V getValueQuiet(Serializable key)
	{
		Element e = getElementQuiet(key);
		return elementToValue(e);
	}
	
	@SuppressWarnings("unchecked")
	private static final <V> V elementToValue(Element e)
	{
		if(e != null)
			return (V)e.getObjectValue();
		
		return null;
	}
}
