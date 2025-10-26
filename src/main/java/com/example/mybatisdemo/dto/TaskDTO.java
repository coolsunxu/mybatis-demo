package com.example.mybatisdemo.dto;

import lombok.*;

/**
 * @author sunxu
 */

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private String name;

    private String deviceId;

    private Short bizType;

    private String content;

    private Short source;

    private String createUserId;

    private Integer expiredTime;

}
