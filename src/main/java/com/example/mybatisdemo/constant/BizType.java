package com.example.mybatisdemo.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunxu
 */

@Getter
@AllArgsConstructor
public enum BizType {

    /**
     * 设备重启
     */
    REBOOT((short)0,"reboot"),


    /**
     * 设备重置
     */
    RESET((short)1,"reset");

    /**
     * 枚举值
     */
    private final Short value;


    /**
     * 枚举描述
     */
    private final String desc;
}
