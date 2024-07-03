package com.github.pdaodao.springwebplus.starter.util;

import cn.hutool.core.util.StrUtil;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SpringUtil extends cn.hutool.extra.spring.SpringUtil {


    public static String getAppName() {
        return getProperty("spring.application.name");
    }

    public static String getContextPath() {
        final String t1 = getProperty("spring.mvc.servlet.path");
        if (StrUtil.isNotBlank(t1) && t1.length() > 1) {
            if (t1.startsWith("/")) {
                return t1;
            }
            return "/" + t1;
        }
        final String t2 = getProperty("server.servlet.context-path");
        if (StrUtil.isNotBlank(t2) && t2.length() > 1) {
            if (t2.startsWith("/")) {
                return t2;
            }
            return "/" + t2;
        }
        return StrUtil.EMPTY;
    }


    public static String getHomePath() {
        ApplicationHome home = new ApplicationHome(SpringUtil.class);
        return home.getDir().getPath();
    }

    public static String getBootPackage() {
        final Class main = getBootClass();
        final String name = main.getName();
        final String pkg = name.substring(0, name.lastIndexOf("."));
        return pkg;
    }

    public static Class getBootClass() {
        Map<String, Object> annotatedBeans = getApplicationContext().getBeansWithAnnotation(SpringBootApplication.class);
        return annotatedBeans.isEmpty() ? null : annotatedBeans.values().toArray()[0].getClass();
    }
}