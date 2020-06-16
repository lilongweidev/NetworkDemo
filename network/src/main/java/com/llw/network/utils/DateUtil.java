package com.llw.network.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {
    //获取当前完整的日期和时间
    public static String getNowDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        return sdf.format(new Date());
    }

    //获取当前日期
    public static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd");
        return sdf.format(new Date());
    }

    //获取当前时间
    public static String getNowTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
    }

    //获取当前日期(精确到毫秒)
    public static String getNowTimeDetail() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    //获取当前日期是星期几
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getTimeStr(){
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(cd.getTime());
    }

    //将时间戳转化为对应的时间(10位或者13位都可以)
    public static String formatTime(long time) {
        String times = null;
        if (String.valueOf(time).length() > 10) {// 10位的秒级别的时间戳
            times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time * 1000));
        } else {// 13位的秒级别的时间戳
            times = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        }
        return times;
    }

    //将时间字符串转为时间戳字符串
    public static String getStringTimestamp(String time) {
        String timestamp = null;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long longTime = sdf.parse(time).getTime() / 1000;
            timestamp = Long.toString(longTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }


    /**
     * 判断两个个时间大小
     * yyyy-MM-dd HH:mm:ss 格式
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int timeCompare(String startTime, String endTime, String nowTime) {
        int result = 0;
        //注意：传过来的时间格式必须要和这里填入的时间格式相同
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date1 = dateFormat.parse(startTime);//开始时间
            Date date2 = dateFormat.parse(endTime);//结束时间
            Date date3 = dateFormat.parse(nowTime);//现在时间

            if (date3.getTime() > date2.getTime()) {//已结束
                result = 1;
            } else if (date3.getTime() < date1.getTime()) {//未开始
                result = 2;
            } else if (date3.getTime() > date1.getTime() && date3.getTime() < date2.getTime()) {//直播中
                result = 3;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
