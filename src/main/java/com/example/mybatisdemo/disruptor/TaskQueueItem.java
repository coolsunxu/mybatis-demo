package com.example.mybatisdemo.disruptor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.CompletableFuture;

/**
 * @author sunxu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskQueueItem {

    private Long busId;

    private String name;

    private String deviceId;

    private Short bizType;

    private Short status;

    private String content;

    private Short source;

    private String createUserId;

    private Long createTime;

    private Integer expiredTime;

    /**
     * 保存同步任务的回调
     */
    private CompletableFuture<Integer> cf;

}

