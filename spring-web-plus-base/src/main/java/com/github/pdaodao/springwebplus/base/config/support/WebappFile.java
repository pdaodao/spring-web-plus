package com.github.pdaodao.springwebplus.base.config.support;

import java.util.HashSet;
import java.util.Set;

public class WebappFile {
    public static final Set<String> FileNames = new HashSet<>();
    public static final Set<String> SubApps = new HashSet<>();

    static {
        FileNames.add("static");
        FileNames.add("public");
        FileNames.add("assets");
    }
}