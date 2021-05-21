package org.oneboot.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 身份证工具类
 * 
 * @author shiqiao.pro
 * 
 */
public class IdentityCardUtils {
	/** 身份证号的第0位 */
	public final static int ZERO = 0;

	/** 身份证号的第4位 */
	public final static int FOUR = 4;
	/** 身份证号的第6位 */
	public final static int SIX = 6;

	/** 身份证号的第8位 */
	public final static int EIGHT = 8;

	/** 身份证号的第10位 */
	public final static int TEN = 10;

	/** 身份证号的第12位 */
	public final static int TWELVE = 12;

	/** 身份证号的第14位 */
	public final static int FOURTEEN = 14;

	/** 身份证号的第15位 */
	public final static int FIFTEEN = 15;

	/** 身份证号的第16位 */
	public final static int SIXTEEN = 16;

	/** 身份证号的第17位 */
	public final static int SEVENTEEN = 17;

	/** 十九世纪 */
	public final static String NINETEEN = "19";

	/** 身份证号模式 */
	public static final String ID_CARD_NUMBER_REG = "^((([0-9]){15})|(([0-9]){17}[0-9Xx]{}))$";

	/** 18位身份证最后的附加位 */
	public final static String OVERHEAD_BIT = "X";

	/** 十九世纪 */
	public final static int THE_19_CENTURY = 1900;

	/** 男性的标识 */
	private static final Set<String> MALE_FLAGS = new HashSet<String>();

	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityCardUtils.class);

	// 初始化男性标识集合
	static {
		MALE_FLAGS.add("1");
		MALE_FLAGS.add("3");
		MALE_FLAGS.add("5");
		MALE_FLAGS.add("7");
		MALE_FLAGS.add("9");
	}

	/**
	 * 根据前17位计算18位身份证号码的最后一位
	 * 
	 * @param idCardNumber17
	 * @return
	 */
	public static int getOverHeadBit(String idCardNumber17) {
		int nSum = 0;

		try {
			for (int nCount = 0; nCount < 17; nCount++) {
				nSum += (Integer.parseInt(idCardNumber17.substring(nCount, nCount + 1))
						* (Math.pow(2, 17 - nCount) % 11));
			}

			nSum %= 11;

			if (nSum <= 1) {
				nSum = 1 - nSum;
			} else {
				nSum = 12 - nSum;
			}
		} catch (Exception ex) {
			LOGGER.warn("计算身份证最后一位时发生异常 ");
		}

		return nSum;
	}

	/**
	 * 校验身份证号码的长度是否有效
	 * 
	 * @param idCardNumber
	 *            身份证号码
	 * @return
	 */
	public static boolean checkIdCardNumberValid(String idCardNumber) {
		if (StringUtils.isBlank(idCardNumber)) {
			return false;
		}

		if (idCardNumber.length() != 18) {
			return false;
		}

		return true;
	}

	/**
	 * 根据身份证号码提取会员的出生年月日(4位年\2位月份\2位日)
	 * 
	 * @param idCardNumber
	 *            身份证号码
	 * @return 出生年月日
	 **/
	public static String getBirthday(String idCardNumber) {
		if (!checkIdCardNumberValid(idCardNumber)) {
			return null;
		}

		String year = null;
		String month = null;
		String day = null;

		String idCardNo = idCardNumber.trim();

		// 处理18位身份证
		if (idCardNo.length() == 18) {
			year = idCardNo.substring(SIX, TEN);
			month = idCardNo.substring(TEN, TWELVE);
			day = idCardNo.substring(TWELVE, FOURTEEN);

		}

		return year + month + day;
	}

	/**
	 * 检查出生日期是否有效。
	 * 
	 * @param idCardNumber
	 *            身份证号码
	 * @return
	 */
	public static boolean checkBirthDayValid(String idCardNumber) {
		try {
			String birthDay = getBirthday(idCardNumber);

			if (StringUtils.isBlank(birthDay)) {
				return false;
			}

			// 提取年月日
			int year = Integer.parseInt(birthDay.substring(ZERO, FOUR));
			int month = Integer.parseInt(birthDay.substring(FOUR, SIX));
			int day = Integer.parseInt(birthDay.substring(SIX, EIGHT));

			// 校验年份
			if ((year < THE_19_CENTURY) || (year > getCurrentYear())) {
				return false;
			}

			// 校验月份
			if ((month < 1) || (month > 12)) {
				return false;
			}

			// 校验日期
			Calendar cal = new GregorianCalendar();
			cal.set(year, month - 1, 1);
			if ((day < 1) || (day > cal.getActualMaximum(Calendar.DAY_OF_MONTH))) {
				return false;
			}

			return true;

		} catch (Exception e) {
			LOGGER.warn("提取生日失败", e);
			return false;
		}

	}

	/**
	 * 取得当前年份。
	 *
	 * @return
	 */
	private static int getCurrentYear() {
		Calendar cal = new GregorianCalendar();

		cal.setTime(new Date());
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 校验身份证号码的长度和格式是否有效
	 * 
	 * @param idCardNumber
	 *            身份证号码
	 * @return
	 */
	public static boolean checkIdCardSchemaValid(String idCardNumber) {
		if (StringUtils.isEmpty(idCardNumber)) {
			return false;
		}

		if (idCardNumber.length() != 18) {
			return false;
		}

		boolean matched = Pattern.matches(ID_CARD_NUMBER_REG, idCardNumber);
		if (!matched) {
			return false;
		}

		String idCardNumber17 = idCardNumber.substring(ZERO, SEVENTEEN);
		String idCandNumber18 = idCardNumber.substring(SEVENTEEN);
		// 计算第18位
		int nSum = getOverHeadBit(idCardNumber17);

		// 如果最后一位是10时实际身份证的最后一位为“X”
		if (nSum == 10) {
			return StringUtils.equalsIgnoreCase(idCandNumber18, OVERHEAD_BIT);
		} else {
			return StringUtils.equals(idCandNumber18, Integer.toString(nSum));
		}

	}
}
