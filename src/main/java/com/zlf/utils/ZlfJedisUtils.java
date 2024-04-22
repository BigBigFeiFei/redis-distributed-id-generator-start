package com.zlf.utils;

import com.zlf.config.RedisProperties;
import com.zlf.enums.ScriptTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
public class ZlfJedisUtils {
    private final String clientName = "zlfRedisId";
    private ConcurrentHashMap<Integer, String> scriptMap1 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> scriptMap2 = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> scriptMap3 = new ConcurrentHashMap<>();
    private List<JedisPool> jdpList = new CopyOnWriteArrayList<>();

    public ZlfJedisUtils(List<RedisProperties> rps) {
        for (int i = 0; i < rps.size(); i++) {
            Integer scriptIndex = i + 1;
            RedisProperties ps = rps.get(i);
            Jedis jedis = this.createJds(ps);

            String luaScript1 = loadScript(ScriptTypeEnum.ONE.getDesc(), scriptIndex);
            String esha1 = jedis.scriptLoad(luaScript1);
            scriptMap1.put(scriptIndex, esha1);

            String luaScript2 = loadScript(ScriptTypeEnum.TWO.getDesc(), scriptIndex);
            String esha2 = jedis.scriptLoad(luaScript2);
            scriptMap2.put(scriptIndex, esha2);

            String luaScript3 = loadScript(ScriptTypeEnum.THREE.getDesc(), scriptIndex);
            String esha3 = jedis.scriptLoad(luaScript3);
            scriptMap3.put(scriptIndex, esha3);
        }
    }

    public Jedis getJedisByIndx(int index) {
        return this.jdpList.get(index).getResource();
    }

    public String getEsha(int index, ScriptTypeEnum scriptTypeEnum) {
        String esha = "";
        if (ScriptTypeEnum.ONE.getScriptType() == scriptTypeEnum.getScriptType()) {
            esha = scriptMap1.get(index);
        } else if (ScriptTypeEnum.TWO.getScriptType() == scriptTypeEnum.getScriptType()) {
            esha = scriptMap2.get(index);
        } else if (ScriptTypeEnum.THREE.getScriptType() == scriptTypeEnum.getScriptType()) {
            esha = scriptMap3.get(index);
        }
        return esha;
    }

    private Jedis createJds(RedisProperties ps) {
        JedisPoolConfig config = new JedisPoolConfig();
        //最大空闲连接数, 应用自己评估，不要超过每个实例最大的连接数
        config.setMaxIdle(ps.getMaxIdle());
        //最大连接数, 应用自己评估，不要超每个实例最大的连接数
        config.setMaxTotal(ps.getMaxTotal());
        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setLifo(true);
        config.setMinIdle(ps.getMinIdle());
        JedisPool jedisPool = StringUtils.isEmpty(ps.getRedisPass()) ? new JedisPool(config, ps.getRedisHost(), ps.getRedisPort(), ps.getConnectionTimeout(), ps.getSoTimeout(), null, ps.getDatabase(), clientName, false) : new JedisPool(config, ps.getRedisHost(), ps.getRedisPort(), ps.getConnectionTimeout(), ps.getSoTimeout(), ps.getRedisPass(), ps.getDatabase(), clientName, false);
        Jedis jedis = jedisPool.getResource();
        jdpList.add(jedisPool);
        return jedis;
    }

    /**
     * 加载lua脚本内容
     *
     * @param scriptDirName script1 script2 script3
     * @param scriptIndex
     * @return
     */
    private String loadScript(String scriptDirName, int scriptIndex) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream is = ZlfJedisUtils.class.getResourceAsStream("/" + scriptDirName + "/redis-script-node" + scriptIndex + ".lua");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
                sb.append("\r\n");//此处必须换行，这是出现RedisCommandExecutionException异常的原因s
            }
            br.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ZlfJedisUtils.loadScript加载第:scriptDirName:{},{}个lua脚本异常", scriptDirName, scriptIndex);
        }
        return sb.toString();
    }
}
