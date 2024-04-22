package com.zlf.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Script1PrefixFormatEnum {

    YD1(5, "年两位 + 当天所在当年第几天(3位),不超过一个世纪内,足够使用"),

    YD2(7, "年四位 + 当天所在当年第几天(3位),如果年取两位的话一个世纪满100年id还是会重复的"),

    YYMMDD(6, "年两位+月+日"),

    YYYYMMDD(8, "年四位+月+日");

    private Integer prefixLength;

    private String desc;

}
