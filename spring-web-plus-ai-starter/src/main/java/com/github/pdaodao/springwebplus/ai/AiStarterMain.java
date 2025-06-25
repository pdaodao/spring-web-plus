package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.base.util.AppUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AiStarterMain {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(AiStarterMain.class, args);
        AppUtil.printlnProjectInfo(context);
    }
}
