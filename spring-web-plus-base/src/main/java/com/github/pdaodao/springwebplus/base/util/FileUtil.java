package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Slf4j
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);


    public static String loadClassPathFileStr(String fileName) throws IOException {
        final Resource resource = new ClassPathResource(fileName);
        try {
            final String rr = IoUtil.readUtf8(resource.getInputStream());
            logger.info("-- load file:" + fileName);
            return rr;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
