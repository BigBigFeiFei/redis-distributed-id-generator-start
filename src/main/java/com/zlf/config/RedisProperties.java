package com.zlf.config;

import lombok.Data;

/**
 * @author zlf
 */
@Data
public class RedisProperties {

    /**
     * redis地址
     */
    private String redisHost;

    /**
     * redis端口
     */
    private int redisPort;

    /**
     * redis密码
     */
    private String redisPass;

    /**
     * 几号库
     */
    private int database = 0;

    /**
     * 连接超时长 默认2000
     */
    private int connectionTimeout = 3000;

    /**
     * socket超时时长 默认2000
     */
    private int soTimeout = 3000;


    /**
     * 最大空闲连接数 默认8
     */
    private int maxIdle = 10;


    /**
     * 最小空闲连接数 默认0
     */
    private int minIdle = 5;

    /**
     * 最大总连接数 默认8
     */
    private int maxTotal = 20;

}
