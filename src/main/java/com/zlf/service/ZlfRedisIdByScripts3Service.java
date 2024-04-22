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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Data
@Service
public class ZlfRedisIdByScripts3Service extends ZlfRedisIdCommonService2 {

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
        throw new RuntimeException("ZlfRedisIdByScripts3Service.loopId生成id异常!");
    }

    @Override
    protected Long nextId(String tab) {
        List<RedisProperties> rps = new CopyOnWriteArrayList(redisConfig.getRps());
        int idx = index++ % rps.size();
        ZlfJedisUtils zlfJedisUtils = (ZlfJedisUtils) ZlfRedisIdSpringUtils.getBean(ZlfJedisUtils.class.getName());
        Jedis jedis = null;
        try {
            jedis = zlfJedisUtils.getJedisByIndx(idx);
            String esha = zlfJedisUtils.getEsha(idx + 1, ScriptTypeEnum.THREE);
            log.info("ZlfRedisIdByScripts3Service.esha:{}", esha);
            List<Long> result = (List<Long>) jedis.evalsha(esha, 2, tab, "" + idx);
            Long id = this.buildId(result.get(0), result.get(1), result.get(2),
                    result.get(3));
            return id;
        } catch (Exception e) {
            log.error("ZlfRedisIdByScripts3Service.nextId生成id异常:{}", e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 构建id
     *
     * @param second
     * @param microSecond
     * @param shardId
     * @param seq
     * @return
     */
    private Long buildId(long second, long microSecond, long shardId,
                         long seq) {
        long miliSecond = (second * 1000 + microSecond / 1000);
        return (miliSecond << (12 + 10)) + (shardId << 10) + seq;
    }

    /**
     * 解析id
     *
     * @param id
     * @return
     */
    public List<Long> parseId(long id) {
        long miliSecond = id >>> 22;
        // 2 ^ 12 = 0xFFF
        long shardId = (id & (0xFFF << 10)) >> 10;
        long seq = id & 0x3FF;
        List<Long> result = new ArrayList<Long>(4);
        result.add(miliSecond);
        result.add(shardId);
        result.add(seq);
        return result;
    }

}
