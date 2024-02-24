package com.github.apengda.springbootplus.core.db;

import cn.hutool.core.lang.ClassScanner;
import com.github.apengda.springbootplus.core.entity.Entity;

import java.util.Set;

public class DbEntityUtil {

    public static void main(String[] args) {
        final Set<Class<?>> cls = ClassScanner.scanAllPackageBySuper("com.github.apengda.springbootplus", Entity.class);
        for (Class clazz : cls) {
            System.out.println(clazz.getName());
        }
    }

}
