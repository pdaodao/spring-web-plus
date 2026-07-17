package com.github.pdaodao.springwebplus.ai;

import com.github.houbb.opencc4j.util.ZhConverterUtil;

public class Test {

    public static void main(String[] args) {
        String toSimp = ZhConverterUtil.toTraditional("介绍一下杭州");
        System.out.println("繁轉簡：" + toSimp);
    }
}
