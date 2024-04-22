package com.zlf.service;

import com.zlf.config.RedisConfig;
import com.zlf.config.RedisProperties;
import com.zlf.dto.GeneratorIdDto;
import com.zlf.enums.Script1PrefixFormatEnum;
import com.zlf.enums.ScriptTypeEnum;
import com.zlf.utils.ZlfJedisUtils;
import com.zlf.utils.ZlfRedisIdSpringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
@Data
@Service
public class ZlfRedisIdByScripts1Service extends ZlfRedisIdCommonService1 {

    private static final DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

    private volatile Integer index = 0;

    @Autowired
    private RedisConfig redisConfig;

    @Override
    protected Long loopId1(String tab, String y_m_d_h_m_s, int len, int retryTimes) {
        Long id = this.loopIdCommon(tab, y_m_d_h_m_s, len, retryTimes);
        if (Objects.nonNull(id)) {
            return id;
        }
        throw new RuntimeException("ZlfRedisIdByScripts1Service.loopId生成id异常!");
    }

    @Override
    protected Long nextId1(String tab, String y_m_d_h_m_s, int len) {
        List<RedisProperties> rps = new CopyOnWriteArrayList(redisConfig.getRps());
        int idx = index++ % rps.size();
        ZlfJedisUtils zlfJedisUtils = (ZlfJedisUtils) ZlfRedisIdSpringUtils.getBean(ZlfJedisUtils.class.getName());
        Jedis jedis = null;
        try {
            jedis = zlfJedisUtils.getJedisByIndx(idx);
            String esha = zlfJedisUtils.getEsha(idx + 1, ScriptTypeEnum.ONE);
            log.info("ZlfRedisIdByScripts1Service.esha:{}", esha);
            Long result = Long.valueOf(jedis.evalsha(esha, 3, tab, y_m_d_h_m_s, String.valueOf(len)).toString());
            return result;
        } catch (Exception e) {
            log.error("ZlfRedisIdByScripts1Service.nextId生成id异常:{}", e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    protected Long nextGeneratorId(GeneratorIdDto dto) {
        this.checkDto(dto);
        String y_m_d_h_m_s = this.getYmdhms(dto.getScript1PrefixFormatEnum());
        Integer len = dto.getLength() - dto.getScript1PrefixFormatEnum().getPrefixLength();
        String tab = this.buildTab(dto);
        log.info("ZlfRedisIdByScripts1Service.tab:{},type:{},length:{},len:{}", tab, dto.getScript1PrefixFormatEnum().getPrefixLength(), dto.getLength(), len);
        Long id = this.loopId1(tab, y_m_d_h_m_s, len, dto.getRetryTimes());
        return id;
    }


    private String getYmdhms(Script1PrefixFormatEnum script1PrefixFormatEnum) {
        String ymdhms3 = dtf.format(LocalDateTime.now());
        log.info("ZlfRedisIdByScripts1Service.nextGenerator.ymdhms3:{}", ymdhms3);
        String y_m_d_h_m_s = "";
        if (Objects.nonNull(script1PrefixFormatEnum)
                && (Script1PrefixFormatEnum.YD1.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength()
                || Script1PrefixFormatEnum.YD2.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength())) {
            String year4 = ymdhms3.substring(0, 4);
            String year2 = year4.substring(2, year4.length());
            String dayOfYear = this.getDayOfYear();
            if (Script1PrefixFormatEnum.YD1.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength()) {
                y_m_d_h_m_s = year2 + dayOfYear;
            } else if (Script1PrefixFormatEnum.YD2.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength()) {
                y_m_d_h_m_s = year4 + dayOfYear;
            }
        }
        if (Objects.nonNull(script1PrefixFormatEnum)
                && Script1PrefixFormatEnum.YYMMDD.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength()) {
            y_m_d_h_m_s = ymdhms3.substring(2, 8);
        }
        if (Objects.nonNull(script1PrefixFormatEnum)
                && Script1PrefixFormatEnum.YYYYMMDD.getPrefixLength() == script1PrefixFormatEnum.getPrefixLength()) {
            y_m_d_h_m_s = ymdhms3.substring(0, 8);
        }
        return y_m_d_h_m_s;
    }

    private String getDayOfYear() {
        String day = "";
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取当天是当年的第几天
        int dayOfYear = today.getDayOfYear();
        if (String.valueOf(dayOfYear).length() == 1) {
            day = "00" + dayOfYear;
        } else if (String.valueOf(dayOfYear).length() == 2) {
            day = "0" + dayOfYear;
        } else if (String.valueOf(dayOfYear).length() == 3) {
            day = String.valueOf(dayOfYear);
        }
        return day;
    }

}
