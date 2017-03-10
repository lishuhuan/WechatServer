package com.demo.util;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class Ehcache {

	private static final CacheManager cacheManager = CacheManager.create();

	private static Cache cache;

	public Ehcache() {
		this.cache = cacheManager.getCache("baseCache");
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	/*
	 * ͨ�����ƴӻ����л�ȡ����
	 */
	@SuppressWarnings("deprecation")
	public static Object getCacheElement(String cacheKey){
		Element e = cache.get(cacheKey);
		if (e == null) {
			return null;
		}
		return e.getValue();
	}

	/*
	 * ��������ӵ�������
	 */
	public static void addToCache(String cacheKey, Object result) throws Exception {
		Element element = new Element(cacheKey, result);
		cache.put(element);
	}

	public static void deleteCache(String key){
    	if(cache.isElementInMemory(key)){
    		cache.remove(key);
    	}
    }
}
