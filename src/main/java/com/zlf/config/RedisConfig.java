package com.zlf.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zlf
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "zlf-redis-id-generator.redis")
public class RedisConfig {

    private List<RedisProperties> rps;

}
