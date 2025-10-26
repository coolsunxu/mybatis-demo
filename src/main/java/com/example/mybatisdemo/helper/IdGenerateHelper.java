package com.example.mybatisdemo.helper;

import com.github.yitter.contract.IdGeneratorOptions;
import com.github.yitter.idgen.YitIdHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author sunxu
 */
@Component
public class IdGenerateHelper {

    @PostConstruct
    public void init() {
        IdGeneratorOptions options = new IdGeneratorOptions((short) 25);
        YitIdHelper.setIdGenerator(options);
    }

    /**
     * 生成分布式ID
     *
     * @return 分布式ID
     */
    public Long nextId() {
        return YitIdHelper.nextId();
    }
}
