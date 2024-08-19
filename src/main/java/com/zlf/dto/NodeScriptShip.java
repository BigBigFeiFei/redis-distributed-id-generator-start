package com.zlf.dto;

import lombok.Data;
import redis.clients.jedis.JedisPool;

/**
 * @author zlf
 */
@Data
public class NodeScriptShip {

    private JedisPool jedisPool;

    private String script;

}
