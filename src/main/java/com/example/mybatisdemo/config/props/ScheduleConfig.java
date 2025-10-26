package com.example.mybatisdemo.config.props;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * @author sunxu
 */
@Validated
@ConfigurationProperties(prefix = "schedule")
@Component
@Data
@NoArgsConstructor
public class ScheduleConfig {

    /**
     * 定期检查的间隔,单位秒
     */
    private int checkInterval;
}
