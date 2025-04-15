package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.*;
import cn.hutool.json.JSONUtil;
import java.nio.charset.Charset;

public class CsvTest {

    public static void main(String[] args) throws Exception{
        final String path = "/Users/peng/Workspace/work2024/spring-web-plus/upload/csv.txt";
        final CsvReader reader = CsvUtil.getReader(FileUtil.getReader(path, Charset.defaultCharset()), CsvReadConfig.defaultConfig());
        reader.stream().forEach(t -> {
            System.out.println(JSONUtil.toJsonStr(t));
            System.out.println("------");
        });
        reader.close();
    }
}
