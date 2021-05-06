package org.oneboot.core.context;

import org.springframework.beans.factory.annotation.Value;

/**
 * 定义当前环境的一些信息
 */
public class ObootContext {
	/** 当前环境信息 */
	private static String env;

	@Value("${application.sys.env}")
	public void setEnv(String env) {
		ObootContext.env = env;
	}

	/**
	 * 是否为开发环境
	 * 
	 * @return
	 */
	public static boolean isDevContext() {
		return "dev".equals(env);
	}

	/**
	 * 是否为测试环境
	 * 
	 * @return
	 */
	public static boolean isTestContext() {
		return "test".equals(env);
	}

	/**
	 * 是否为生产环境
	 * 
	 * @return
	 */
	public static boolean isProdContext() {
		return "prod".equals(env);
	}

	/**
	 * 是否为沙箱环境
	 * 
	 * @return
	 */
	public static boolean isSandboxieContext() {
		return "sandboxie".equals(env);
	}
}
