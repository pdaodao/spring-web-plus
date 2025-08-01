package com.github.pdaodao.flow;

import com.github.pdaodao.springwebplus.base.util.AppUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FlowMain {

    public static void main(String[] args) throws Exception {
        System.setProperty("springdoc.default-flat-param-object", "true");
        ConfigurableApplicationContext context = SpringApplication.run(FlowMain.class, args);
        AppUtil.printlnProjectInfo(context);
    }
    
}