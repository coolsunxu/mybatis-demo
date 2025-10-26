package com.example.mybatisdemo;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.example.mybatisdemo.mapper.DeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Collections;

@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MybatisDemoApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    public void testMybatisPlusGenerator() {
        // 数据库地址
        String url = "jdbc:mysql://127.0.0.1:3306/mybatisdb?useUnicode=true&serverTimezone=UTC&characterEncoding=UTF-8";

        // 用户名
        String username = "root";

        // 密码
        String password = "123456";

        // 数据表名
        String tableName = "device";

        // 作者
        String author = "coolsunxu";

        // 获取项目路径
        String projectDir = System.getProperty("user.dir");

        // 代码生成路径
        String outputDir = projectDir + "\\src\\main\\java\\";

        // 设置父包名
        String parent = "com.example";

        // 设置模块名
        String moduleName = "mybatisdemo";

        // 映射文件生成路径
        String xmlPath = projectDir + "\\src\\main\\resources\\com\\example\\mybatisdemo\\mapper";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(gc -> {
                    gc.author(author)
                            .fileOverride()
                            .outputDir(outputDir);
                })
                .packageConfig(builder -> {
                    builder.parent(parent)
                            .moduleName(moduleName)
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, xmlPath));
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tableName);
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

}
