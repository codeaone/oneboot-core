package org.oneboot.core.lang;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 常用日期工具类
 * 
 * @author shiqiao.pro
 * 
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
	/** yyyyMMdd */
	public final static String SHORT_FORMAT = "yyyyMMdd";

	/** yyyyMMddHHmmss */
	public final static String LONG_FORMAT = "yyyyMMddHHmmss";

	/** yyyy-MM-dd */
	public final static String WEB_FORMAT = "yyyy-MM-dd";
	/** yyyy-MM-dd HH:mm:ss */
	public final static String WEB_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** HHmmss */
	public final static String TIME_FORMAT = "HHmmss";

	/** yyyyMM */
	public final static String MONTH_FORMAT = "yyyyMM";

	/** yyyy年MM月dd日 */
	public final static String CHINA_FORMAT = "yyyy年MM月dd日";

	/** yyyy-MM-dd HH:mm */
	public final static String WEB_FORMAT_NO_SEC = "yyyy-MM-dd HH:mm";

	/** yyyy-MM-dd'T'HH:mm:ss */
	public final static String CNAPS_STANDARD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
			"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM", "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss",
			"yyyy.MM.dd HH:mm", "yyyy.MM" };

	/**
	 * 获取当前Date型日期
	 * 
	 * @return Date() 当前日期
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 获取当前日期, 默认格式为<code>yyyy-MM-dd</code>
	 * 
	 * @return String
	 */
	public static String getDate() {
		return format(getNowDate(), WEB_FORMAT);
	}
	
	/**
	 * 日期路径 即年/月/日 如20180808
	 */
	public static final String dateTime() {
		Date now = new Date();
		return DateFormatUtils.format(now, "yyyyMMdd");
	}

	/**
	 * 获取当前日期, 默认格式为<code>yyyy-MM-dd HH:mm:ss</code>
	 * 
	 * @return
	 */
	public static final String getDateTime() {
		return format(getNowDate(), WEB_TIME_FORMAT);
	}

	/**
	 * 返回 yyyy-MM-dd 格式字符串
	 * 
	 * @param date
	 * @return
	 */
	public static final String dateTime(final Date date) {
		return format(date, WEB_FORMAT);
	}

	/**
	 * 日期格式化基础方法
	 * 
	 * @param date   待格式化的日期对象
	 * @param format 输出的格式
	 * @return 格式化的字符串
	 */
	public static String format(Date date, String format) {
		if (date == null || StringUtils.isBlank(format)) {
			return StringUtils.EMPTY;
		}

		return DateFormatUtils.format(date, format);
	}

	/**
	 * 日期字符串解析成日期对象基础方法，可以在此封装出多种便捷的方法直接使用
	 * 
	 * @param dateStr 日期字符串
	 * @param format  输入的格式
	 * @return 日期对象
	 * @throws ParseException
	 */
	public static Date parse(String dateStr, String format) throws ParseException {
		if (StringUtils.isBlank(format)) {
			throw new IllegalArgumentException("format can not be null.");
		}
		return parseDate(dateStr, format);
	}

	/**
	 * 通用的转换方法
	 * 
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateStr) throws ParseException {
		return parseDate(dateStr, parsePatterns);
	}

	/**
	 * 格式化当前时间
	 * 
	 * @param format 输出的格式
	 * @return
	 */
	public static String formatCurrent(String format) {
		if (StringUtils.isBlank(format)) {
			return StringUtils.EMPTY;
		}

		return format(new Date(), format);
	}

	/**
	 * 把日期对象按照<code>yyyyMMdd</code>格式解析成字符串
	 * 
	 * @param date 待格式化的日期对象
	 * @return 格式化的字符串
	 */
	public static String formatShort(Date date) {
		return format(date, SHORT_FORMAT);
	}

	/**
	 * 把日期对象按照<code>yyyy-MM-dd</code>格式解析成字符串
	 * 
	 * @param date 待格式化的日期对象
	 * @return 格式化的字符串
	 */
	public static String formatWeb(Date date) {
		return format(date, WEB_FORMAT);
	}

	/**
	 * 把日期对象按照<code>yyyy-MM-dd HH:mm:ss</code>格式解析成字符串
	 * 
	 * @param date 待格式化的日期对象
	 * @return 格式化的字符串
	 */
	public static String formatWebTime(Date date) {
		return format(date, WEB_TIME_FORMAT);
	}

	/**
	 * 把日期对象按照<code>yyyyMM</code>格式解析成字符串
	 * 
	 * @param date 待格式化的日期对象
	 * @return 格式化的字符串
	 */
	public static String formatMonth(Date date) {
		return format(date, MONTH_FORMAT);
	}

	/**
	 * 把日期对象按照<code>HHmmss</code>格式解析成字符串
	 * 
	 * @param date 待格式化的日期对象
	 * @return 格式化的字符串
	 */
	public static String formatTime(Date date) {
		return format(date, TIME_FORMAT);
	}

	/**
	 * 获取yyyyMMddHHmmss+n位随机数格式的时间戳
	 * 
	 * @param n 随机数位数
	 * @return
	 */
	public static String getTimestamp(int n) {
		return formatCurrent(LONG_FORMAT) + RandomStringUtils.randomNumeric(n);
	}

	/**
	 * 根据日期格式返回昨日日期
	 * 
	 * @param format 日期格式
	 * @return
	 */
	public static String getYesterdayDate(String format) {
		return getDateCompareToday(format, -1, 0);
	}

	/**
	 * 把当日日期作为基准，按照格式返回相差一定间隔的日期
	 *
	 * @param format     日期格式
	 * @param daysAfter  和当日比相差几天，例如3代表3天后，-1代表1天前
	 * @param monthAfter 和当日比相差几月，例如2代表2月后，-3代表3月前
	 * @return
	 */
	public static String getDateCompareToday(String format, int daysAfter, int monthAfter) {
		Calendar today = Calendar.getInstance();
		if (daysAfter != 0) {
			today.add(Calendar.DATE, daysAfter);
		}
		if (monthAfter != 0) {
			today.add(Calendar.MONTH, monthAfter);
		}
		return format(today.getTime(), format);
	}

	/**
	 * 根据日期格式返回上月的日期
	 * 
	 * @param format
	 * @return
	 */
	public static String getLastMonth(String format) {
		Calendar today = Calendar.getInstance();
		today.add(Calendar.MONTH, -1);
		return format(today.getTime(), format);
	}

	/**
	 * 得到当前时间的毫秒数，并转为String类型
	 */
	public static String getCurrentTimeMillis() {
		return String.valueOf(System.currentTimeMillis());
	}

	/**
	 * 得到当前时间的秒，并转为String类型
	 * 
	 * @return
	 */
	public static String getCurrentTimeSeconds() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
}
