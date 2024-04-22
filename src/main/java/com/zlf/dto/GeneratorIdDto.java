package com.zlf.dto;

import com.zlf.enums.Script1PrefixFormatEnum;
import lombok.Data;

@Data
public class GeneratorIdDto {

    /**
     * 必传
     */
    private String applicationName;

    /**
     * 必传
     */
    private String tabName;

    /**
     * ZlfRedisIdByScripts1Service-必传
     * 其它选传
     */
    private Integer length;

    /**
     * ZlfRedisIdByScripts1Service-选传
     * 其它没有
     */
    private Script1PrefixFormatEnum script1PrefixFormatEnum = Script1PrefixFormatEnum.YD2;

    /**
     * 选传
     */
    private Integer retryTimes = 5;

}
