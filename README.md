# redis-distributed-id-generator-start

redis-distributed-id-generator-start启动器<br>
1. 项目中引入依赖如下：<br>
```
<dependency>
    <groupId>io.github.bigbigfeifei</groupId>
    <artifactId>redis-distributed-id-generator-start</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```        
2. nacos配置如下：<br>
```
## 配置需要保证唯一不重复(eqps中的每一的index唯一,一般配置成递增的,队列交换机绑定关系的bean注入都是根据rps的List下标+eqps中index下标注入保证了唯一性)
zlf-redis-id-generator:
  redis:
    rps:
      - redis-host: xxxx1
        redis-port: 6379
        redis-pass: 12345678
      - redis-host: xxxx2
        redis-port: 6379
        redis-pass: 12345678
      - redis-host: xxxx3
        redis-port: 6379
        redis-pass: 12345678 
```
3. 启动类上加入如下注解：<br>
@EnableZlfRedisId 开启redis分布式id生成器功能