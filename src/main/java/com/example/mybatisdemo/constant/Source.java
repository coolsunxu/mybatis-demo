package com.example.mybatisdemo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunxu
 */


@Getter
@AllArgsConstructor
public enum Source {
    /**
     * web
     */
    WEB((short) 0, "web"),

    OPENAPI((short) 1, "openapi"),

    CPE((short) 2, "cpe");


    /**
     * 枚举值
     */
    private final Short value;


    /**
     * 枚举描述
     */
    private final String desc;


}
