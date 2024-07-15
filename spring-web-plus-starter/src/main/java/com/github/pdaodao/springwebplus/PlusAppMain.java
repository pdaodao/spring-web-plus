package com.github.pdaodao.springwebplus;

import com.github.pdaodao.springwebplus.base.util.AppUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class PlusAppMain {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(PlusAppMain.class, args);
        AppUtil.printlnProjectInfo(context);
    }
}
