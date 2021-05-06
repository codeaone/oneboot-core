package org.oneboot.core.cache.local.expire;

import java.util.Map;
import java.util.TimerTask;

public class ExpireCacheDeleteTask extends TimerTask {

	@Override
	public void run() {
		// 循环Map中数据，找到已经过期的数据进行删除
		for (Map.Entry<String, ExpireCacheObject> entry : ExpireLocalMapCache.CACHE.entrySet()) {
			if (entry.getValue().getExpire() == 0) {
				// 为0的跳过
				break;
			}
			Long expire = System.currentTimeMillis() - entry.getValue().getCurrentTime();
			if (expire > entry.getValue().getExpire()) {
				ExpireLocalMapCache.remove(entry.getKey());
			}
		}
	}

}
