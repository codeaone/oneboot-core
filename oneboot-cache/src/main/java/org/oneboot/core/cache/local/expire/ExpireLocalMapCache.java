package org.oneboot.core.cache.local.expire;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过ConcurrentHashMap实现简单的本地缓存，增加过期机制（惰性+定期删除） </br>
 * </br>
 * 本地缓存适用于数据访问频繁，数据本身稳定，不会轻易修改的情况下，在这里我们利用ConcurrentHashMap实现一个简单的本地缓存 </br>
 * 该本地缓存具有时效性，如果超过一定时间没有被使用则被清空，使其系统中不会使用到过期数据
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public final class ExpireLocalMapCache {

	/** 本地缓存 **/
	public static final Map<String, ExpireCacheObject> CACHE = new ConcurrentHashMap<>(16);

	/**
	 * 定时删除器
	 */
	private static Timer deleteTimer = null;

	/**
	 * 默认删除时间(秒) 5分钟
	 */
	private static final int DEFAULT_DELETE_SECONDS = 5 * 60;

	/**
	 * 初始化定时器
	 * 
	 * @param deleteSeconds
	 */
	public static void initTimer(int deleteSeconds) {

		if (deleteSeconds == 0) {
			deleteSeconds = DEFAULT_DELETE_SECONDS;
		}

		deleteTimer = new Timer();
		deleteTimer.schedule(new ExpireCacheDeleteTask(), 0, deleteSeconds * 1000);
	}

	/**
	 * 没有设置有效时间，默认永久
	 * 
	 * @param key
	 * @param value
	 */
	public static void put(String key, Object value) {
		CACHE.put(key, new ExpireCacheObject(value));
	}

	/**
	 * 有过期时间的放入，存放时间大于0, 时间单位毫秒
	 * 
	 * @param key
	 * @param value
	 * @param expire
	 */
	public static void put(String key, Object value, long expire) {
		CACHE.put(key, new ExpireCacheObject(value, expire));
	}

	/**
	 * 清楚所有
	 */
	public static void removeAll() {
		CACHE.clear();
	}

	/**
	 * 移除某个key的值
	 * 
	 * @param key
	 */
	public static void remove(String key) {
		CACHE.remove(key);
	}

	/**
	 * 获取值，惰性检查过期时间，最后返回有效值
	 * 
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		ExpireCacheObject obj = CACHE.get(key);
		if (obj == null) {
			return null;
		}
		if (obj.getExpire() > 0) {
			Long expire = System.currentTimeMillis() - obj.getCurrentTime();
			if (expire > obj.getExpire()) {
				remove(key);
				return null;
			}
		}
		return obj.getValue();
	}

}
