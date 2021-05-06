package org.oneboot.core.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.oneboot.core.lang.StringUtils;
import org.oneboot.core.utils.IdentityCardUtils;
import org.springframework.util.CollectionUtils;

/**
 * 各类数据格式检查工具集合
 * 
 * @author shiqiao.pro
 * @see https://boot.codeaone.com
 */
public class ValidateUtils {

	/** Email格式的正则表达式 */
	public final static String EMAIL_REG = "^([a-zA-Z0-9_\\.\\-\\+])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,20})$";

	/** 大陆身份证的正则表达式 */
	public final static String ID_CARD_NUMBER_REG = "^((([0-9]){15})|(([0-9]){17}[0-9Xx]{1}))$";

	/** 18位身份证最后的附加位 */
	public final static String OVERHEAD_BIT = "X";

	/** 正则表达式:验证IP地址 */
	public static final String REGEX_IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";

	/** 客户姓名的正则表达式 */
	private static final String CUST_NAME_REGEX = "^([\u4e00-\u9fa5]+|[a-zA-Z]+)((·|\\s)?([\u4e00-\u9fa5]+|[a-zA-Z]+))*";

	/** 组织机构代码正则表达式 */
	private static final String ORG_CODE_REGEX = "^([0-9A-Z]){8}-[0-9|X]$";

	/** 年月正则表达式 */
	private static final String YEAR_MONTH_REGEX = "^(?:19|20\\d{2})(?:0[1-9]|1[0-2])";

	/** 经度正则表达式 */
	private static final String LONGITUDE_REGEX = "^[\\-\\+]?(0?\\d{1,2}\\.\\d{1,5}|1[0-7]?\\d{1}\\.\\d{1,5}|180\\.0{1,5})$";
	/** 纬度正则表达式 */
	private static final String LATITUDE_REGEX = "^[\\-\\+]?([0-8]?\\d{1}\\.\\d{1,5}|90\\.0{1,5})$";

	/** 身份证地址正则表达式 */
	private static final String ADDRESS_REGEX = "^[0-9a-zA-Z-()\\s（）\\._\u4e00-\u9fa5]+$";

	// public static void main(String[] args) {
	// // String mobile = "19951296969";
	// // System.out.println(isValidMobile(mobile));
	// // String month = "201803";
	// // System.out.println(isYearMonth(month));
	// //（整数部分为0～180，必须输入1到5位小数）
	// String longitude = "179.111110";
	// //纬度（整数部分为0～90，必须输入1到5位小数）
	// String latitude = "79.11111";
	// System.out.println(isLongitude(longitude));
	// System.out.println(isLatitude(latitude));
	//
	// }

	/**
	 * 经度数据检查
	 * 
	 * @param longitude
	 * @return
	 */
	public static boolean isLongitude(final String longitude) {
		if (StringUtils.isBlank(longitude)) {
			return false;
		}
		if (Pattern.matches(LONGITUDE_REGEX, longitude)) {
			return true;
		}
		return false;
	}

	/**
	 * 纬度数据检查
	 * 
	 * @param latitude
	 * @return
	 */
	public static boolean isLatitude(final String latitude) {
		if (StringUtils.isBlank(latitude)) {
			return false;
		}
		if (Pattern.matches(LATITUDE_REGEX, latitude)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为年月
	 * 
	 * @param month
	 * @return
	 */
	public static boolean isYearMonth(final String month) {
		if (StringUtils.isBlank(month)) {
			return false;
		}
		if (Pattern.matches(YEAR_MONTH_REGEX, month)) {
			return true;
		}

		return false;
	}

	/**
	 * 是否合法地址
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isValidAddress(final String address) {
		if (StringUtils.isBlank(address) || address.length() > 128) {
			return false;
		}
		return address.matches(ADDRESS_REGEX);
	}

	/**
	 * 组织结构代码是否合法
	 * 
	 * @param orgCode
	 * @return
	 */
	public static boolean isValidOrgCode(final String orgCode) {
		if (StringUtils.isBlank(orgCode)) {
			return false;
		}
		return Pattern.matches(ORG_CODE_REGEX, StringUtils.trim(orgCode).toUpperCase());
	}

	/**
	 * 校验身份证上的名字合法性，也就是中国的名字
	 * 
	 * @param realName
	 * @return
	 */
	public static boolean isValidIdCardName(final String realName) {
		if (StringUtils.isEmpty(realName)) {
			return false;
		}

		if (StringUtils.isBlank(realName) || realName.length() < 2 || realName.length() > 20) {
			return false;
		}
		return Pattern.matches(CUST_NAME_REGEX, realName);
	}

	/**
	 * 校验IP地址
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isValidIp(final String ip) {
		if (StringUtils.isEmpty(ip)) {
			return false;
		}
		return Pattern.matches(REGEX_IP_ADDR, StringUtils.trim(ip));
	}

	/**
	 * 判断身份证是否有效:包括身份证长度、年龄
	 * 
	 * @param idCard
	 *            大陆身份证
	 * @return true 身份证有效，false 身份证无效
	 */
	public static boolean isValidIdCard(final String idCard) {
		if (StringUtils.isEmpty(idCard)) {
			return false;
		}

		// 身份证基本格式校验
		if (!IdentityCardUtils.checkIdCardSchemaValid(idCard)) {
			return false;
		}

		// 校验生日是否有效
		if (!IdentityCardUtils.checkBirthDayValid(idCard)) {
			return false;
		}

		return true;
	}

	/**
	 * 是否符合手机格式要求,1开头的11位数字
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 */
	public static boolean isValidMobile(final String mobile) {
		if (StringUtils.isBlank(mobile)) {
			return false;
		}
		// 这里的格式检查比较宽松
		if (mobile.startsWith("1") && (mobile.length() == 11) && 
				StringUtils.isNumeric(StringUtils.trim(mobile))) {
			return true;
		}

		return false;
	}

	/**
	 * 检查邮箱是否符合格式要求
	 *
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(final String email) {
		if (StringUtils.isBlank(email)) {
			return false;
		}
		return Pattern.matches(EMAIL_REG, StringUtils.trim(email));
	}

	/**
	 * 是否符合邮编要求,6位位数字
	 * 
	 * @param mobile
	 *            手机号
	 * @return
	 */
	public static boolean isValidPostCode(final String PostCode) {
		if (StringUtils.isBlank(PostCode)) {
			return false;
		}
		if ((PostCode.length() == 6) && StringUtils.isNumeric(StringUtils.trim(PostCode))) {
			return true;
		}
		return false;
	}
	
	 private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	    public static <T> ValidationResult validateEntity(T obj) {
	        ValidationResult result = new ValidationResult();
	        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
	        if (!CollectionUtils.isEmpty(set)) {
	            result.setHasErrors(true);
	            Map<String, String> errorMsg = new HashMap<String, String>();
	            for (ConstraintViolation<T> cv : set) {
	                errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
	            }
	            result.setErrorMsg(errorMsg);
	        }
	        return result;
	    }

	    public static <T> ValidationResult validateProperty(T obj, String propertyName) {
	        ValidationResult result = new ValidationResult();
	        Set<ConstraintViolation<T>> set = validator.validateProperty(obj, propertyName, Default.class);
	        if (!CollectionUtils.isEmpty(set)) {
	            result.setHasErrors(true);
	            Map<String, String> errorMsg = new HashMap<String, String>();
	            for (ConstraintViolation<T> cv : set) {
	                errorMsg.put(propertyName, cv.getMessage());
	            }
	            result.setErrorMsg(errorMsg);
	        }
	        return result;
	    }

	    public static String normalizeMobile(String mobile) {
	        if (StringUtils.isBlank(mobile)) {
	            return mobile;
	        }
	        // 去除+开头的字串
	        mobile = chopBeginWith(mobile, "+");
	        // 去除0、00开头的字串
	        mobile = chopBeginWith(mobile, "0");
	        mobile = chopBeginWith(mobile, "0");

	        // 判断是否为包含-的标准格式
	        if (mobile.contains("-")) {
	            // 去除大陆手机号前的86-
	            mobile = chopBeginWith(mobile, "86-");
	        } else {
	            // 特殊不加-的大陆手机号,去除86开头的字串
	            while (mobile.startsWith("86")) {
	                mobile = chopBeginWith(mobile, "86");
	            }
	        }

	        return mobile;
	    }
	    
	    /**
	     * 删除指定字符开头的字串
	     * 
	     * @param mobile
	     * @param separator
	     * @return
	     */
	    private static String chopBeginWith(String mobile, String separator) {
	        if (mobile.startsWith(separator)) {
	            mobile = mobile.substring(separator.length(), mobile.length());
	        }
	        return mobile;
	    }
}
