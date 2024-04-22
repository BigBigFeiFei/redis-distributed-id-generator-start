package com.zlf.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.commons.net.ntp.TimeStamp;

import java.io.DataOutputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class ClockSyncUtils {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    //"ntp1.aliyun.com", "ntp2.aliyun.com"

    /**
     * pool.ntp.org还是比较权威的,使用这个就可以
     */
    public void clockSyncTime(String server) {
        if (StringUtils.isBlank(server)) {
            server = "pool.ntp.org";
        }
        //1,在系统属性里面添加如下一条:
        System.setProperty("user.timezone", "Asia/Shanghai");
        //2,把默认时区改成我们的时区:
        //TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        //TimeZone.setDefault(tz);
        int timeout = 20000; // 设置超时时间为30秒
        NTPUDPClient timeClient = new NTPUDPClient();
        timeClient.setDefaultTimeout(timeout);
        try {
            // 设置NTP服务器的地址
            // 获取时间
            timeClient.open();
            TimeInfo timeInfo = timeClient.getTime(InetAddress.getByName(server));
            timeClient.close();
            // 将返回的时间转换为毫秒
            TimeStamp timeStamp = timeInfo.getMessage().getTransmitTimeStamp();
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            long returnTimeMillis = returnTime - timeInfo.getMessage().getOriginateTimeStamp().getTime();
            // 输出同步后的时间
            log.info("ClockSyncUtils.NTP-Time:{}", returnTimeMillis);
            // 获取NTP服务器传回的原始时间值
            long ntpTime = timeInfo.getReturnTime();
            // 获取和设置系统当前时间
            long systemTime = System.currentTimeMillis();
            long difference = systemTime - ntpTime;
            log.info("clockSyncTime.difference:{}", difference);
            if (difference != 0) {
                Date date = timeStamp.getDate();
                Instant instant = date.toInstant();
                LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String ldtStr = dtf.format(ldt);
                String syncDate = ldtStr.substring(0, 10);
                String syncTime = ldtStr.substring(11);
                String osType = System.getProperty("os.name");
                if (osType.toLowerCase().startsWith("win")) {
                    log.info("=============ClockSyncUtils.系统是windows系统============");
                    //修改应用服务器年月日
                    Runtime.getRuntime().exec("cmd /c date " + syncDate);
                    //修改应用服务器时分秒
                    Runtime.getRuntime().exec("cmd /c time " + syncTime);
                } else {
                    log.info("=============ClockSyncUtils.系统是Linux系统==============");
                    // 获取当前用户权限
                    Process process = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes("date -s \"" + ldtStr + "\"\n");
                    os.flush();
                    os.writeBytes("exit\n");
                    os.flush();
                    process.waitFor();
                    os.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("ClockSyncUtils同步时间异常:{}", e.getMessage());
        }
    }

}
