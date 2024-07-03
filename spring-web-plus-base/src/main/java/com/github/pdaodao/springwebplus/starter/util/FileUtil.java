package com.github.pdaodao.springwebplus.starter.util;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    public static String loadClassPathFileStr(String fileName) throws IOException {
        final Resource resource = new ClassPathResource(fileName);
        try {
            return IoUtil.readUtf8(resource.getInputStream());
        } catch (Exception e) {
            System.out.println("file [" + fileName + "] does not exist");
            throw new IOException(e);
        }
    }
}
