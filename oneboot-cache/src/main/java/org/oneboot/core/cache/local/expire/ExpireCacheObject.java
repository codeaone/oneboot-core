package org.oneboot.core.cache.local.expire;

import org.oneboot.core.logging.ToString;

public class ExpireCacheObject extends ToString {

	/** serialVersionUID **/
	private static final long serialVersionUID = 4430093119187279473L;

	private Long currentTime;

	private Object value;

	/** 过期时间，单位为毫秒 **/
	private Long expire = 0L;

	public ExpireCacheObject(Object value, Long expire) {
		super();
		this.value = value;
		this.expire = expire;
		this.currentTime = System.currentTimeMillis();
	}

	public ExpireCacheObject(Object value) {
		super();
		this.value = value;
		this.currentTime = System.currentTimeMillis();
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public Long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}
