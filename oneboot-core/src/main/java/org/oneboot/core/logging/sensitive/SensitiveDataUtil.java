package org.oneboot.core.logging.sensitive;

import org.oneboot.core.lang.StringUtils;

/**
 * 敏感数据屏蔽工具类
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class SensitiveDataUtil {

	/** 是否进行敏感数据屏蔽的开关 */
	private static boolean hideFlag = true;

	/**
	 * 对银行卡号进行部分隐藏处理，只显示前6位和后4位，其他用*代替。<br/>
	 * 不对数据做格式检查
	 * 
	 * @param bankCardNo
	 * @return
	 */
	public static String bankCardNoHide(final String bankCardNo) {
		if (!needHide()) {
			return bankCardNo;
		}

		if (isBlank(bankCardNo)) {
			return bankCardNo;
		}
		return customizeHide(bankCardNo, 6, 4, bankCardNo.length() - 10);
	}

	/**
	 * 判断是否为空字符串
	 * 
	 * @param bankCardNo
	 * @return
	 */
	private static boolean isBlank(final String str) {
		return StringUtils.isBlank(str);
	}

	/**
	 * 自定义屏蔽位数和屏蔽位置
	 * 
	 * <pre>
	 * SensitiveDataUtil.customizeHide("14709091234",3,4,4) = "147****1234"
	 * SensitiveDataUtil.customizeHide("14709091234",0,4,4) = "****1234"
	 * SensitiveDataUtil.customizeHide("14709091234",3,0,4) = "147****"
	 * SensitiveDataUtil.customizeHide("14709091234",3,0,8) = "147********"
	 * </pre>
	 * 
	 * @param str    原数据
	 * @param front  展示前几位
	 * @param tail   展示后几位
	 * @param hidden 展示星号*的个数
	 * @return 部分隐藏的敏感数据字符串
	 */
	public static String customizeHide(final String str, final int front, final int tail, final int hidden) {
		if (isBlank(str)) {
			return str;
		}
		String tmp = str.trim();
		int length = tmp.length();
		// 合法性检查，如果参数不合法，返回源数据内容
		if (front < 0 || tail < 0 || hidden < 0 || front + tail > length) {
			return tmp;
		}

		int beginIndex = front - 1;
		int endIndex = length - tail;

		// 原数据前半部分
		StringBuilder result = new StringBuilder();
		if (beginIndex >= 0 && beginIndex < length) {
			result.append(tmp.substring(0, front));
		}

		// 中间*
		for (int i = 0; i < hidden; i++) {
			result.append('*');
		}

		// 原数据后半部分
		if (endIndex >= 0 && endIndex < length) {
			result.append(tmp.substring(endIndex));
		}

		return result.toString();
	}

	/**
	 * 对居民身份证号码进行部分隐藏处理，只显示前5位和后2位，其他用*代替。<br/>
	 * 不对数据做格式检查
	 * 
	 * @param idCardNo
	 * @return
	 */
	public static String idCardNoHide(String idCardNo) {
		if (!needHide()) {
			return idCardNo;
		}

		if (isBlank(idCardNo)) {
			return idCardNo;
		}
		return customizeHide(idCardNo, 5, 2, idCardNo.length() - 7);
	}

	/**
	 * 手机号码通用隐藏规则（包括港澳台地区），隐藏中间四位 适用于网站以及客户端 <br/>
	 * <li>SensitiveDataUtil.mobileHide("13012345678") = 130****5678
	 * <li>SensitiveDataUtil.mobileHide("3012345678") = 30****678
	 * <li>SensitiveDataUtil.mobileHide("012345678") = 01****78
	 * <li>SensitiveDataUtil.mobileHide("812348") = 8****8 <br/>
	 * <br/>
	 * 不对数据做格式检查
	 * 
	 * @param mobile
	 * @return
	 */
	public static String mobileHide(String mobile) {
		if (!needHide()) {
			return mobile;
		}

		if (isBlank(mobile)) {
			return mobile;
		}
		String tmp = mobile.trim();
		int notHideNum = tmp.length() - 4;
		return customizeHide(tmp, notHideNum / 2, notHideNum - notHideNum / 2, 4);
	}

	/**
	 * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
	 */
	public static String chineseName(final String fullName) {
		if (StringUtils.isBlank(fullName)) {
			return "";
		}
		final String name = StringUtils.left(fullName, 1);
		return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
	}

	/**
	 * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
	 */
	public static String chineseName(final String familyName, final String givenName) {
		if (StringUtils.isBlank(familyName) || StringUtils.isBlank(givenName)) {
			return "";
		}
		return chineseName(familyName + givenName);
	}

	/**
	 * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
	 *
	 * @param sensitiveSize 敏感信息长度
	 */
	public static String address(final String address, final int sensitiveSize) {
		if (StringUtils.isBlank(address)) {
			return "";
		}
		final int length = StringUtils.length(address);
		return StringUtils.rightPad(StringUtils.left(address, length - sensitiveSize), length, "*");
	}

	/**
	 * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
	 */
	public static String email(final String email) {
		if (StringUtils.isBlank(email)) {
			return "";
		}
		final int index = StringUtils.indexOf(email, "@");
		if (index <= 1) {
			return email;
		} else {
			return StringUtils.rightPad(StringUtils.left(email, 1), index, "*")
					.concat(StringUtils.mid(email, index, StringUtils.length(email)));
		}
	}
	  /**
	   * [公司开户银行联号] 公司开户银行联行号,显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
	   */
	  public static String cnapsCode(final String code) {
	    if (StringUtils.isBlank(code)) {
	      return "";
	    }
	    return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
	  }
	 

	public static void setHideFlag(final boolean hideFlag) {
		SensitiveDataUtil.hideFlag = hideFlag;
	}

	public static boolean needHide() {
		return hideFlag;
	}

}
