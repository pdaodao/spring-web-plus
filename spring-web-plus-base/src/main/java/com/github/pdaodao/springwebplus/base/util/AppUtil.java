package com.github.pdaodao.springwebplus.base.util;


import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class AppUtil {
    private static final String BACKSLASH = "/";


    /**
     * 打印项目信息
     *
     * @param context
     */
    public static void printlnProjectInfo(ConfigurableApplicationContext context) {
        try {
            ConfigurableEnvironment environment = context.getEnvironment();
            String serverPort = environment.getProperty("server.port");
            String contextPath = environment.getProperty("server.servlet.context-path");
            if (!BACKSLASH.equals(contextPath)) {
                contextPath = contextPath + BACKSLASH;
            }
            System.out.println("usr.dir:"+System.getProperty("user.dir"));
            String localhostDocUrl = "\nhttp://localhost:" + serverPort + contextPath + "doc.html";
            System.out.println(localhostDocUrl);
            String localhostSwaggerUrl = "http://localhost:" + serverPort + contextPath + "swagger-ui/index.html";
            System.out.println(localhostSwaggerUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}