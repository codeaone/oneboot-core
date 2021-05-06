package org.oneboot.core.cache.session;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.oneboot.core.cache.local.expire.ExpireLocalMapCache;
import org.oneboot.core.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

public class RedisSessionClient implements SessionClient {

	private static Logger logger = LoggerFactory.getLogger(RedisSessionClient.class);

	private RedisTemplate<String, Object> redisTemplate;

	/** ValueOperations 理解成Map<Object,Object> **/
	private final ValueOperations<String, Object> valueOps;

	private Random rnd;

	// 应该要写在配置中
	private int sessionIdLength = 32;

	/** 本地缓存删除task时长， (秒) 5分钟 **/
	private int deleteSeconds = 5 * 60;

	@Autowired
	public RedisSessionClient(RedisTemplate<String, Object> redisTemplate) {
		super();
		this.redisTemplate = redisTemplate;
		this.valueOps = redisTemplate.opsForValue();
		try {
			this.rnd = new SecureRandom();
		} catch (Throwable e) {
			this.rnd = new Random();
		}
		ExpireLocalMapCache.initTimer(deleteSeconds);
	}

	@Override
	public boolean refreshTimeout(String token, int time) {
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("token is null");
		}

		try {
			if (time > 0) {
				redisTemplate.expire(token, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean putSession(String token, Object value, int expire) {
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("token is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("set value is null");
		}

		try {
			valueOps.set(token, value);
			// 如果为0，不设置过期时间。
			if (expire > 0) {
				redisTemplate.expire(token, expire, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Object getSession(String token) {
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("token is null");
		}

		return valueOps.get(token);
	}

	@Override
	public Object getSessionLocal(String token) {
		// 在从redis中获取前，先从本地获取，当本地没有，再去远程获取
		// 在使用时，还是要注意一些，当在用户体量非常大时，此方案会占用不少资源
		Object obj = ExpireLocalMapCache.get(token);
		if (obj != null) {
			// 从严格来讲，这里可能缓存内容本身已经过期了，不过这个场景的特殊性，就忽略吧
			return obj;
		}
		obj = getSession(token);
		if (obj != null) {
			ExpireLocalMapCache.put(token, obj);
		}
		return obj;
	}

	@Override
	public boolean removeSession(String token) {
		if (StringUtils.isBlank(token)) {
			throw new IllegalArgumentException("token is null");
		}

		try {
			redisTemplate.delete(token);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	@Override
	public String genSessionId() {
		String id = StringUtils.EMPTY;
		while (true) {
			id = generateSessionId();
			Object v = getSession(id);
			if (v != null) {
				logger.error("生成sessionid重复!sessionid={}", id);
				id = null;
			} else {
				break;
			}

		}
		return id;
	}

	public String generateSessionId() {
		byte[] bytes = new byte[((sessionIdLength + 3) / 4) * 3];
		rnd.nextBytes(bytes);
		String value = new String(Base64.encodeBase64(bytes), 0, sessionIdLength).replace('/', '$');

		return value;
	}

}
