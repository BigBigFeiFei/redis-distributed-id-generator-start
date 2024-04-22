package com.zlf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 脚本类型枚举
 */
@Getter
@AllArgsConstructor
public enum ScriptTypeEnum {


    ONE(1, "script1"),

    TWO(2,"script2"),

    THREE(3,"script3");

    private Integer ScriptType;

    private String desc;

}

