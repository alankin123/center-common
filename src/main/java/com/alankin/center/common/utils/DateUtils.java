/**
 * All rights reserved by XinGuoDu Inc.
 */
package com.alankin.center.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 日期工具类. <p>
 * @author lansongtao
 * @Date 2015年8月17日
 * @since 1.0
 */
public class DateUtils {
    /** 日期格式yyyyMMdd.*/
    public static final String DATE_FORMAT_YYYY = "yyyy";

    /** 日期格式yyyyMMdd.*/
    public static final String DATE_FORMAT1 = "yyyyMMdd";

    /** 日期格式yyyy-MM-dd.*/
    public static final String DATE_FORMAT2 = "yyyy-MM-dd";
    
    /** 日期格式yyMMdd.*/
    public static final String DATE_FORMAT3 = "yyMMdd";

    /** 日期格式yyyyMMddHHmmss.*/
    public static final String DATETIME_FORMAT1 = "yyyyMMddHHmmss";

    /** 日期格式yyyy-MM-dd HH:mm:ss.*/
    public static final String DATETIME_FORMAT2 = "yyyy-MM-dd HH:mm:ss";
    
    /** 日期格式yyyyMMddHHmmssSSS.*/
    public static final String DATETIME_FORMAT3 = "yyyyMMddHHmmssSSS";
    
    /** 日期格式MMddHHmmss.*/
    public static final String DATETIME_FORMAT4 = "MMddHHmmss";
    /**HHmmss*/
    public static final String DATETIME_FORMAT5="HHmmss";

    /**
     * 获取服务器系统时间.
     * @return			服务器系统时间
     */
    public static Date getSystemDate() {
        return new Date();
    }

    /**
     * 去掉日期的小时分秒.<p>
     * @param date			日期
     * @return				当天开始时间
     */
    public static Date truncateDate(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
            0, 0);
        return calendar.getTime();
    }

    /**
     * 获取服务器系统日期.
     * @return			服务器系统日期
     */
    public static Date getSystemTruncateDate() {
        return truncateDate(new Date());
    }

    /**
     * 日期增加或减少天数 .<p>
     * @param date			日期
     * @param day			增加或减少天数(负数为减少)
     * @return				日期
     */
    public static Date addDay(final Date date, final int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
            0, 0);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 日期增加或减少天数 .<p>
     * @param date			日期
     * @param day			增加或减少天数(负数为减少)
     * @return				日期
     */
    public static Date addMonth(final Date date, final int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
            0, 0);
        calendar.add(Calendar.MONTH, month);
        return calendar.getTime();
    }
    
    /**
     * 日期转换为字符串.
     * @param date				日期
     * @param dateFormat		格式
     * @return					字符串
     */
    public static String date2String(Date date, String dateFormat) {
        if(date==null){
            return null;
        }
        return new SimpleDateFormat(dateFormat).format(date);
    }

    /**
     * 日期转换为字符串.<p>
     * 日期格式为yyyyMMdd
     * @param date				日期
     * @return					字符串
     */
    public static String date2String(Date date) {
        return date2String(date, DATE_FORMAT1);
    }

    /**
     * 日期转换为字符串.<p>
     * 日期格式为yyyyMMddHHmmss
     * @param date				日期
     * @return					字符串
     */
    public static String datetime2String(Date date) {
        return date2String(date, DATETIME_FORMAT1);
    }

    /**
     * 日期转换为长整型.<p>
     * 日期格式为yyyyMMdd
     * @param date				日期
     * @return					字符串
     */
    public static long date2Long(Date date) {
        return Long.valueOf(date2String(date, DATE_FORMAT1));
    }

    /**
     * 日期转换为长整型.<p>
     * 日期格式yyyyMMddHHmmss
     * @param date				日期
     * @return					长整型
     */
    public static long dateTime2Long(Date date) {
        return Long.valueOf(date2String(date, DATETIME_FORMAT1));
    }

    /**
     * 字符串转换为日期.
     * @param dateString		字符串
     * @param dateFormat		格式
     * @return					日期
     */
    public static Date string2Date(String dateString, String dateFormat) {
        if(StringUtils.isEmpty(dateString)){
            return null;
        }
        DateFormat df = new SimpleDateFormat(dateFormat);
        try {
            return df.parse(dateString);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("字符串格式的日期和日期格式不匹配，转换发生错误！目标格式："+dateFormat, e);
        }
    }

    /**
     * 字符串转换为日期.<p>
     * 日期格式为yyyyMMdd
     * @param dateString		字符串
     * @return					日期
     */
    public static Date string2Date(String dateString) {
        try {
            return string2Date(dateString, DATE_FORMAT1);
        }
        catch (Exception e) {
            return string2Date(dateString, DATETIME_FORMAT1);
        }
    }

    /**
     * 长整型转换为日期.<p>
     * 日期格式为yyyyMMdd
     * @param dateString		字符串
     * @return					日期
     */
    public static Date long2Date(long date) {
        return string2Date(String.valueOf(date), DATE_FORMAT1);
    }

    /**
     * 字符串转换为日期.<p>
     * 日期格式yyyyMMddHHmmss
     * @param dateString		字符串
     * @return					日期
     */
    public static Date string2DateTime(String dateString) {
        try {
            return string2Date(dateString, DATETIME_FORMAT1);
        }
        catch (Exception e) {
            return string2Date(dateString, DATE_FORMAT1);
        }
    }

    /**
     * 长整型转换为日期.<p>
     * 日期格式yyyyMMddHHmmss
     * @param dateString		字符串
     * @return					日期
     */
    public static Date long2DateTime(long date) {
        return string2Date(String.valueOf(date), DATETIME_FORMAT1);
    }

    /**
     * 返回yyyyMMdd格式的当前日期
     */
    public static String nowDateStr() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT1);
        return sdf.format(new java.util.Date());
    }

    /**
     * 当前日期-n天
     */
    public static Date getBeforeDate(Date date, int n) {
    	return addDay(date, n * -1);
    }

    /**  
    * 取得两个时间段的时间间隔  
    * return t2 与t1的间隔天数  
    * throws ParseException 如果输入的日期格式不是0000-00-00 格式抛出异常  
    */
    public static int getBetweenDays(String t1, String t2) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        int betweenDays = 0;
        Date d1 = format.parse(t1);
        Date d2 = format.parse(t2);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        // 保证第二个时间一定大于第一个时间
        if (c1.after(c2)) {
            c1 = c2;
            c2.setTime(d1);
        }
        int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
        for (int i = 0; i < betweenYears; i++) {
            betweenDays += countDays(c1.get(Calendar.YEAR));
            c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
        }
        return betweenDays;
    }
    
    /**  
     * 取得两个时间段的时间间隔  
     * return t2 与t1的间隔天数  
     * throws ParseException 如果输入的日期格式不是0000-00-00 格式抛出异常  
     */
     public static int getBetweenDays(Date t1, Date t2) throws ParseException {
         int betweenDays = 0;
         Calendar c1 = Calendar.getInstance();
         Calendar c2 = Calendar.getInstance();
         c1.setTime(t1);
         c2.setTime(t2);
         // 保证第二个时间一定大于第一个时间
         if (c1.after(c2)) {
             c1 = c2;
             c2.setTime(t1);
         }
         int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
         betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
         for (int i = 0; i < betweenYears; i++) {
             betweenDays += countDays(c1.get(Calendar.YEAR));
             c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
         }
         return betweenDays;
     }

    /**
     * 内部方法
     * @param year
     * @return
     */
    private static int countDays(int year) {
        int n = 0;
        for (int i = 1; i <= 12; i++) {
            n += countDays(i, year);
        }
        return n;
    }

    /**
     * 内部方法
     * @param month
     * @param year
     * @return
     */

    private static int countDays(int month, int year) {
        int count = -1;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                count = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                count = 30;
                break;
            case 2:
                if (year % 4 == 0)
                    count = 29;
                else
                    count = 28;
                if ((year % 100 == 0) & (year % 400 != 0))
                    count = 28;
        }
        return count;
    }

    /**
     * 将MMDD字符串转为YYYYMMDD字符串
     * @param str
     * @return
     */
    public static String getYYYYMMDD(String str) {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        int strMonth = Integer.valueOf(str.substring(0, 2));
        if (strMonth == 0) {
            return "";
        }
        if (month < strMonth) {
            year = year - 1;
        }
        str = year + "" + str;
        return str;
    }

    /**
     * 得到 yyyyMMdd 格式的指定日期的前一天
     */
    public static String foreDay(String day) {
        DateFormat daydf = new SimpleDateFormat(DATE_FORMAT1);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(string2Date(day, DATE_FORMAT1).getTime());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return daydf.format(cal.getTime());
    }
    
    /***
     * 
     * 计算相差多少天
     * @param date1 开始时间
     * @param date2 结束时间
     * @return
     * @throws Exception
     * @author pengjinhui
     * @since 2015年12月9日下午3:38:29
     */
    public static long getDistanceDays(Date date1, Date date2){
        long days=0;
        long time1 = date1.getTime();  
        long time2 = date2.getTime();
        long diff ;  
        if(time1<time2) {  
            diff = time2 - time1;  
        } else { 
            diff = time1 - time2;
        }  
        days = diff / (1000 * 60 * 60 * 24);
        return days;
    }
    /**
     * 获取某月第一天
     * 功能描述
     * @param yyyyMM
     * @return
     * @author helei
     * @since 2015年12月16日下午9:29:56
     */
    public static String getFirstDayByYYYYMM(String yyyyMM){
        Date date1 = DateUtils.string2Date(yyyyMM, "yyyy-MM");
        return DateUtils.date2String(date1, DateUtils.DATE_FORMAT2);
    }
    /**
     * 获取某月最后一天
     * 功能描述
     * @param yyyyMM
     * @return
     * @author helei
     * @since 2015年12月16日下午9:29:56
     */
    public static String getLastDayByYYYYMM(String yyyyMM){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = DateUtils.string2Date(yyyyMM, "yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);    
        String day_first = df.format(calendar.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        
        return str.toString();
    }
    /**
     * 获取某年第一天
     * 功能描述
     * @param yyyyMM
     * @return
     * @author helei
     * @since 2015年12月16日下午9:29:56
     */
    public static String getFirstDayByYYYY(String yyyy){
        Date date1 = DateUtils.string2Date(yyyy, "yyyy");
        return DateUtils.date2String(date1, DateUtils.DATE_FORMAT2);
    }
    /**
     * 获取当年最后一天
     * 功能描述
     * @param yyyy
     * @return
     * @author helei
     * @since 2015年12月16日下午9:33:24
     */
    public static String getLastDayByYYYY(String yyyy){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = DateUtils.string2Date(yyyy, "yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        calendar.add(Calendar.YEAR, 1);
        calendar.add(Calendar.DATE, -1);    
        String day_first = df.format(calendar.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        
        return str.toString();
    }
    
    /**
     * 转换MMddHHmmss为日期
     */
    public static Date getDateBymmddHHmiss(Date accoutingDate,String mmddHHmiss){
        if(StringUtils.isEmpty(mmddHHmiss)){
            return null;
        }
        String year = DateUtils.date2String(accoutingDate,DateUtils.DATE_FORMAT_YYYY);
        return DateUtils.string2Date(year+mmddHHmiss,DateUtils.DATETIME_FORMAT1);
    }
    
    /**
     * 
     * 转换成字符中yyyyMMddHHmmss
     * @param accoutingDate
     * @param mmddHHmiss
     * @return
     * @author pengjinhui
     * @since 2016年2月22日下午2:12:56
     */
    public static String getStrBymmddHHmiss(Date accoutingDate,String mmddHHmiss){
        if(StringUtils.isEmpty(mmddHHmiss)){
            return null;
        }
        String accountDateStr=DateUtils.date2String(accoutingDate);
        long year=Long.parseLong(accountDateStr.substring(0,4));
        String transTime=year+mmddHHmiss.substring(0,4);
        if(Long.parseLong(transTime)>Long.parseLong(accountDateStr)){
            year=year-1;
        }
        
        //String year = DateUtils.date2String(accoutingDate,DateUtils.DATE_FORMAT_YYYY);
        return year+mmddHHmiss;
    }
    
    /**
     * 转换MMdd为日期
     */
    public static Date getDateBymmdd(Date accoutingDate,String mmdd){
        if(StringUtils.isEmpty(mmdd)){
            return null;
        }
        String year = DateUtils.date2String(accoutingDate,DateUtils.DATE_FORMAT_YYYY);
        return DateUtils.string2Date(year+mmdd,DateUtils.DATE_FORMAT1);
    }
    /**
     *  转换成字符中转换yyyyMMdd为日期
     */
    public static String getStrBymmdd(Date accoutingDate,String mmdd){
        if(StringUtils.isEmpty(mmdd)){
            return null;
        }
        String accountDateStr=DateUtils.date2String(accoutingDate);
        long year=Long.parseLong(accountDateStr.substring(0,4));
        String transTime=year+mmdd;
        if(Long.parseLong(transTime)>Long.parseLong(accountDateStr)){
            year=year-1;
        }
        return year+mmdd;
    }
    
    
    /**
     * 转换MMdd为日期
     */
    public static Date addMinutes(Date date,int minutes){
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(date);
        nowTime.add(Calendar.MINUTE, minutes);
        return nowTime.getTime();
    }
    
    public static Date addSeconds(Date date,int seconds)
    {
    	Calendar nowTime = Calendar.getInstance();
        nowTime.setTime(date);
        nowTime.add(Calendar.SECOND, seconds);
        return nowTime.getTime();
    }
    
    /**
     * 
     * 功能描述  时间戳转日期
     * @param strDateTime
     * @return
     * @author ZM
     * @since 2016年1月18日下午2:10:40
     */
    public static String getTimestamp2Date(String strDateTime) {
        String d = null;
        if (!StringUtils.isEmpty(strDateTime)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            d = df.format(new Date(new Long(strDateTime)));
        }
        return d;
    }
    
    /**
     * 功能描述  时间戳转日期时间
     * @description 
     * @return String
     * @param strDateTime
     * @return
     * @author ZM
     */
    public static String getTimestamp2DateTime(String strDateTime)
    {
    	String d = null;
        if (!StringUtils.isEmpty(strDateTime)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            d = df.format(new Date(new Long(strDateTime)));
        }
        return d;
    }
    
    /**
     * 功能描述  时间戳转时间
     * @description 
     * @return String
     * @param strDateTime
     * @return
     * @author chengji
     * @date 2016年6月7日 下午8:17:49
     */
    public static String getTimestamp2Time(String strDateTime)
    {
    	String d = null;
        if (!StringUtils.isEmpty(strDateTime)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = df.format(new Date(new Long(strDateTime)));
        }
        return d;
    }
    
    
    /**
     * 
     * 功能描述  yyyy-MM-dd HH:mm:ss
     * @param strDate
     * @return
     * @author ZM
     * @since 2016年1月18日下午2:46:33
     */
    public static Date getStr2DateTime(String strDate) {
        Date d = null;
        if (!StringUtils.isEmpty(strDate)) {
            SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT2);
            try {
               d = df.parse(strDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
                return d;
            }
        }
        
        return d;
    }
    
    /**
     * 
     * 取当前小时
     * @return
     * @author Z^HE
     * @since 2016年4月20日上午10:52:55
     */
    public static int getCurrentHours(Date date){
        String str = DateUtils.getDate2Str(date==null?new Date():date, DateUtils.DATETIME_FORMAT5);
        return Integer.parseInt(str.substring(0,2));
    }
    
    /**
     * 
     * 功能描述
     * @param date
     * @param formatStr
     * @return
     * @author ZM
     * @since 2016年3月22日下午5:41:11
     */
    public static String getDate2Str(Date date,String formatStr) {
        String d = null;
        if (date!=null && !date.equals("")) {
            SimpleDateFormat df = new SimpleDateFormat(formatStr);
            d = df.format(date);
        }
        return d;
    }
    
}
