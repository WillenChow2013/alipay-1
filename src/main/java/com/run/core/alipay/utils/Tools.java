package com.run.core.alipay.utils;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;

public class Tools {
	
	private static  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
	public static String toTimeFormat() {
		return sdf.format(new Date());
	}

	/**
	 * 正则表达式：验证手机号
	 */
	public static final String REGEX_MOBILE = "^((19[0-9])|(16[0-9])|(17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

	/**
	 * 校验手机号
	 *
	 * @param mobile
	 * @return 校验通过返回true，否则返回false
	 */
	public static boolean isMobile(String mobile) {
		return Pattern.matches(REGEX_MOBILE, mobile);
	}

	public static String urlEncode(String str) {
		String result = null;
		try {
			result = URLEncoder.encode(str, "UTF-8");
		} catch (Exception ex) {
			result = "";
		}
		return result;
	}

	public static String getRandomString(int length) {
		// 定义一个字符串（A-Z，a-z，0-9）即62位；
		String str = "zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		// 由Random生成随机数
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		// 长度为几就循环几次
		for (int i = 0; i < length; ++i) {
			// 产生0-61的数字
			int number = random.nextInt(62);
			// 将产生的数字通过length次承载到sb中
			sb.append(str.charAt(number));
		}
		// 将承载的字符转换成字符串
		return sb.toString();
	}

	public static String md5Encrypt(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes("UTF-8");
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toUpperCase();
		} catch (Exception e) {
			return null;
		}
	}

	public static Timestamp getMinTime() {
		return Tools.toDateTime("1970-01-01 00:00:00");
	}

	public static Timestamp getCurrentTime() {
		return new Timestamp(System.currentTimeMillis());
	}

	public static String toDateString(Timestamp t) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(t);
	}

	public static String toDateStringMonth(Timestamp t) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		return formatter.format(t);
	}

	public static String toDateTimeString(Timestamp t) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(t);
	}

	// milliseconds �Ӽ��ĺ�����
	public static Timestamp toDate(String dateString, long milliseconds) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(dateString);
		} catch (Exception ex) {
			return getCurrentTime();
		}
		long l = date.getTime();
		l += milliseconds;
		return new Timestamp(l);
	}

	public static Timestamp toDateTime(String dateTimeString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = formatter.parse(dateTimeString);
		} catch (Exception ex) {
			return getCurrentTime();
		}
		return new Timestamp(date.getTime());
	}

	public static String millisecondsToDate(long milliseconds) {
		Date d = new Date(milliseconds);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(d);
	}

	public static String millisecondsToDates(String datetime) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		LocalDateTime ldt = LocalDateTime.parse(datetime, dtf);
		DateTimeFormatter fa = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String datetime2 = ldt.format(fa);
		return datetime2;
	}

	public static String addDays(String date, int days) {
		String result = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date d = format.parse(date);
			Calendar ca = Calendar.getInstance();
			ca.setTime(d);
			ca.add(Calendar.DATE, days);
			result = format.format(ca.getTime());
		} catch (Exception ex) {
		}
		return result;
	}

	public static int getAllNumberToInt(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return Integer.valueOf(m.replaceAll("").trim());
	}

	public static String getPastDate(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - day);
		Date today = calendar.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String result = format.format(today);
		return result;
	}

	public static List<String> getPastDates(int day) {
		List<String> result = new ArrayList<String>();

		for (int d = 0; d < day; d++)
			result.add(getPastDate(d));

		return result;
	}

	/**
	 * 获取过去任意天内的日期数组
	 * 
	 * @param intervals intervals天内
	 * @return 日期数组
	 */
	public static ArrayList<String> lastDate(int intervals) {
		ArrayList<String> pastDaysList = new ArrayList<>();
		for (int i = 0; i < intervals; i++) {
			pastDaysList.add(getPastDate(i));
		}
		return pastDaysList;
	}

	// JAVA获取某段时间内每天的日期（String类型，格式为："2018-06-16"）
	public static List<String> findDaysStr(String begintTime, String endTime) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dBegin = null;
		Date dEnd = null;
		try {
			dBegin = sdf.parse(begintTime);
			dEnd = sdf.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 存放每一天日期String对象的daysStrList
		List<String> daysStrList = new ArrayList<String>();
		// 放入开始的那一天日期String
		daysStrList.add(sdf.format(dBegin));

		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);

		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);

		// 判断循环此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，给定的日历字段增加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			String dayStr = sdf.format(calBegin.getTime());
			daysStrList.add(dayStr);
		}


		return daysStrList;
	}
	
	public static String getPinYinHeadChar(String str) {
		
		String convert = "";
		String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";
		for(int j = 0; j < str.length(); j++) {
			char word = str.charAt(j);
			String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
			
			if (pinyinArray != null) {
				if((pinyinArray[0].charAt(0) + "").matches(regex))
					convert += (pinyinArray[0].charAt(0) + "").toUpperCase();
            }else
            	convert += word;
		}
		return convert;
		
	}
	
}
