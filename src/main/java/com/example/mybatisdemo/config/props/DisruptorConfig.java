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
@ConfigurationProperties(prefix = "disruptor")
@Component
@Data
@NoArgsConstructor
public class DisruptorConfig {
    /**
     * 线程池名前缀
     */
    private String prefix;

    /**
     * 队列总数
     */
    private Integer queueSize;

    /**
     * 每个队列的容量
     */
    private Integer bufferSize;
}
