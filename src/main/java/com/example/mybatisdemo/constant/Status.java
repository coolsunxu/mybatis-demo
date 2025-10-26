package com.example.mybatisdemo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sunxu
 */

@Getter
@AllArgsConstructor
public enum Status {

    /**
     * 待执行
     */
    PENDING((short)0,"待执行"),

    /**
     * 执行中
     */
    PROCESSING((short)1,"执行中"),


    /**
     * 执行成功
     */
    SUCCEED((short)2,"成功"),


    /**
     * 执行失败
     */
    FAILED((short)3,"失败"),


    /**
     * 任务取消
     */
    CANCELED((short)4,"取消");

    /**
     * 枚举值
     */
    private final Short value;


    /**
     * 枚举描述
     */
    private final String desc;

}
