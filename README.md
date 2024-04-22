# redis-distributed-id-generator-start

redis-distributed-id-generator-start启动器<br>
1.项目中引入依赖如下：<br>
```
<dependency>
    <groupId>io.github.bigbigfeifei</groupId>
    <artifactId>redis-distributed-id-generator-start</artifactId>
    <version>1.0</version>
</dependency>
```        
2.nacos配置如下：<br>
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
3.启动类上加入如下注解：<br>
@EnableZlfRedisId 开启redis分布式id生成器功能<br>

4.使用<br>
```
package xxxx.controller;

import com.zlf.dto.GeneratorIdDto;
import com.zlf.service.ZlfRedisIdByScripts1Service;
import com.zlf.service.ZlfRedisIdByScripts2Service;
import com.zlf.service.ZlfRedisIdByScripts3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("id")
public class IdGcontroller {

    @Autowired
    private ZlfRedisIdByScripts1Service zlfRedisIdByScripts1Service;

    @Autowired
    private ZlfRedisIdByScripts2Service zlfRedisIdByScripts2Service;

    @Autowired
    private ZlfRedisIdByScripts3Service zlfRedisIdByScripts3Service;

    @GetMapping("getId1")
    public Long getId1() {
        GeneratorIdDto dto = new GeneratorIdDto();
        dto.setApplicationName("t_id1");
        dto.setTabName("id1");
        dto.setLength(16);
        return zlfRedisIdByScripts1Service.generatorIdByLength(dto);
    }

    @GetMapping("getId2")
    public Long getId2() {
        GeneratorIdDto dto = new GeneratorIdDto();
        dto.setApplicationName("t_id2");
        dto.setTabName("id2");
        return zlfRedisIdByScripts2Service.generatorId(dto);
    }

    @GetMapping("getId3")
    public Long getId3() {
        GeneratorIdDto dto = new GeneratorIdDto();
        dto.setApplicationName("t_id3");
        dto.setTabName("id3");
        return zlfRedisIdByScripts3Service.generatorId(dto);
    }

}

```