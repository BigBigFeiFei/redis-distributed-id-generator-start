package com.zlf.starter;

import com.alibaba.fastjson.JSON;
import com.zlf.config.RedisConfig;
import com.zlf.config.RedisProperties;
import com.zlf.utils.ZlfJedisUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;
import java.util.Objects;

/**
 * @author zlf
 */
@Data
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Jedis.class, JedisPoolConfig.class})
@EnableConfigurationProperties(RedisConfig.class)
public class ZlfRedisIdRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {

    private BeanFactory beanFactory;

    private RedisConfig redisConfig;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        List<RedisProperties> rps = redisConfig.getRps();
        if (CollectionUtils.isEmpty(rps)) {
            throw new RuntimeException("RedisConfig的rps配置不为空,请检查配置!");
        }
        if (rps.size() > 3) {
            throw new RuntimeException("RedisConfig的rps配置目前只支持3节点配置,请拓展节点及lua脚本!");
        }
        log.info("zlf.ZlfRedisIdRegistrar:rps.size:{},rps:{}", rps.size(), JSON.toJSONString(rps));
        for (int i = 0; i < rps.size(); i++) {
            this.checkRedisProperties(rps.get(i));
        }
        ZlfJedisUtils zlfJedisUtils = new ZlfJedisUtils(rps);
        ((ConfigurableBeanFactory) this.beanFactory).registerSingleton(ZlfJedisUtils.class.getName(), zlfJedisUtils);
        log.info("zlf.ZlfRedisIdRegistrar注册完成,beanName:{}", ZlfJedisUtils.class.getName());
    }

    /**
     * 检查redisConfig配置的主要参数
     *
     * @param redisProperties
     */
    private void checkRedisProperties(RedisProperties redisProperties) {
        String redisHost = redisProperties.getRedisHost();
        if (StringUtils.isEmpty(redisHost)) {
            throw new RuntimeException("RedisProperties.redisHost不为空");
        }
        int port = redisProperties.getRedisPort();
        if (Objects.isNull(port)) {
            throw new RuntimeException("RedisProperties.port不为空");
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        // 通过Binder将environment中的值转成对象
        redisConfig = Binder.get(environment).bind(getPropertiesPrefix(RedisConfig.class), RedisConfig.class).get();
    }

    private String getPropertiesPrefix(Class<?> tClass) {
        return Objects.requireNonNull(AnnotationUtils.getAnnotation(tClass, ConfigurationProperties.class)).prefix();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

}
