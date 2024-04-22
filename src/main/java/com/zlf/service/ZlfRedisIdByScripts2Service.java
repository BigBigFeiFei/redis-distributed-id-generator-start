package com.zlf.service;

import com.zlf.config.RedisConfig;
import com.zlf.config.RedisProperties;
import com.zlf.dto.GeneratorIdDto;
import com.zlf.enums.ScriptTypeEnum;
import com.zlf.utils.ZlfJedisUtils;
import com.zlf.utils.ZlfRedisIdSpringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
@Service
public class ZlfRedisIdByScripts2Service extends ZlfRedisIdCommonService2 {

    private volatile Integer index = 0;

    @Autowired
    private RedisConfig redisConfig;

    @Override
    protected Long nextGeneratorId(GeneratorIdDto dto) {
        this.checkDto(dto);
        Long id = this.loopId(this.buildTab(dto), dto.getRetryTimes());
        return id;
    }

    @Override
    protected Long loopId(String tab, int retryTimes) {
        Long id = this.loopIdCommon(tab, retryTimes);
        if (Objects.nonNull(id)) {
            return id;
        }
        throw new RuntimeException("ZlfRedisIdByScripts2Service.loopId生成id异常!");
    }

    @Override
    protected Long nextId(String tab) {
        List<RedisProperties> rps = new CopyOnWriteArrayList(redisConfig.getRps());
        int idx = index++ % rps.size();
        ZlfJedisUtils zlfJedisUtils = (ZlfJedisUtils) ZlfRedisIdSpringUtils.getBean(ZlfJedisUtils.class.getName());
        Jedis jedis = null;
        try {
            jedis = zlfJedisUtils.getJedisByIndx(idx);
            String esha = zlfJedisUtils.getEsha(idx + 1, ScriptTypeEnum.TWO);
            log.info("ZlfRedisIdByScripts2Service.esha:{}", esha);
            Long result = Long.valueOf(String.valueOf(jedis.evalsha(esha, 1, tab)));
            return result;
        } catch (Exception e) {
            log.error("ZlfRedisIdByScripts2Service.nextId生成id异常:{}", e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

}
