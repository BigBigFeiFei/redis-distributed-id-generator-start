package com.zlf.starter;

import com.zlf.service.ZlfRedisIdByScripts1Service;
import com.zlf.service.ZlfRedisIdByScripts2Service;
import com.zlf.service.ZlfRedisIdByScripts3Service;
import com.zlf.utils.ZlfRedisIdSpringUtils;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用需要在启动类上加入@EnableZlfRedisId注解开启自动装配
 *
 * @author zlf
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({ZlfRedisIdRegistrar.class, ZlfRedisIdByScripts1Service.class, ZlfRedisIdByScripts2Service.class, ZlfRedisIdByScripts3Service.class, ZlfRedisIdSpringUtils.class})
public @interface EnableZlfRedisId {

}
