package com.github.apengda.springwebplus.starter.db;

import cn.hutool.core.lang.ClassScanner;
import com.github.apengda.springwebplus.starter.entity.Entity;

import java.util.Set;

public class DbEntityUtil {

    public static void main(String[] args) {
        final Set<Class<?>> cls = ClassScanner.scanAllPackageBySuper("com.github.apengda.springwebplus", Entity.class);
        for (Class clazz : cls) {
            System.out.println(clazz.getName());
        }
    }

}
