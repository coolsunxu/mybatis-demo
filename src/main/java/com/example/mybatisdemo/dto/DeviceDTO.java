package com.example.mybatisdemo.dto;


import com.example.mybatisdemo.deserializer.StrictIntegerDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDTO {

    private String deviceId;
    private Boolean online;

    // 自定义参数检查，定义序列化解析器，此步之后@Valid的注解才会生效
    @JsonDeserialize(using = StrictIntegerDeserializer.class)
    private Integer length;

}
