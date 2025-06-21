package com.github.pdaodao.aicompare;

import com.github.pdaodao.springwebplus.base.util.AppUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = ElasticsearchClientAutoConfiguration.class)
public class AiCompareMain {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(AiCompareMain.class, args);
        AppUtil.printlnProjectInfo(context);
    }
}
