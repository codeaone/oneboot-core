package org.oneboot.core.cache.session;

/**
 * 分布式会话客户端
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public interface SessionClient {

	/**
	 * 刷新过期时间
	 * 
	 * @param token
	 * @param time
	 * @return
	 */
	boolean refreshTimeout(String token, int time);

	/**
	 * 把一个对象放入cache,并设置过期时间。
	 * 
	 * <p>
	 * 注：value的大小要小于64K
	 * </p>
	 * 
	 * @param key
	 *            保证Cache服务器中的唯一性。 <code>Not Null</code>
	 * @param value
	 *            保存的值。 <code>Not Null</code>
	 * @param expire
	 *            过期时间(单位为秒)，0 表示长期 <code>Not Null</code>
	 * @return 保存成功, 返回<code>true</code>, 否则, 返回<code>false</code>
	 */
	boolean putSession(String token, Object value, int expire);

	/**
	 * 取出一个对象
	 * 
	 * @param key
	 *            保证Cache服务器中的唯一性。 <code>Not Null</code>
	 * @return 获取的对象
	 */
	Object getSession(String token);

	/**
	 * 获取session，优先从本地缓存中获取，主要是可以解决高访问带来的性能问题
	 * 
	 * @param token
	 * @return
	 */
	Object getSessionLocal(String token);

	/**
	 * 删除一个对象
	 * 
	 * @param key
	 *            <code>Not Null</code>
	 * @return 删除成功, 返回<code>true</code>, 否则, 返回<code>false</code>
	 */
	boolean removeSession(String token);

	/**
	 * 生成 session id 随机数
	 * 
	 * @return
	 */
	String genSessionId();
}
