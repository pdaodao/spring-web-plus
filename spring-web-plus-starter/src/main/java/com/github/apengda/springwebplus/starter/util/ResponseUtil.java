package com.github.apengda.springwebplus.starter.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.github.apengda.springwebplus.starter.pojo.RestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.server.MimeMappings;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ResponseUtil {

    public static void writeFile(final String filePath, String fileName, HttpServletResponse response, boolean isAttach) throws Exception {
        final File file = FileUtil.file(filePath);
        if (!FileUtil.exist(file)) {
            throw RestException.notFound404("404");
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = FileUtil.getName(file);
        }
        setHead(fileName, response, isAttach);
        try (final OutputStream outputStream = response.getOutputStream()) {
            FileUtil.writeToStream(file, outputStream);
        }
    }


    public static void writeFile(String fileName, final InputStream inputStream, HttpServletResponse response, boolean isAttach) throws Exception {
        if (inputStream == null) {
            throw RestException.notFound404("404");
        }
        setHead(fileName, response, isAttach);
        try (final OutputStream outputStream = response.getOutputStream()) {
            IoUtil.copy(inputStream, outputStream);
        }
    }


    /**
     * @param fileName 文件名称
     * @param bytes    文件二进制
     * @param response
     * @param isAttach 是否是附件的方式
     * @throws Exception
     */
    public static void writeFile(String fileName, final byte[] bytes, HttpServletResponse response, boolean isAttach) throws Exception {
        if (bytes == null || bytes.length < 10) {
            throw RestException.notFound404("404");
        }
        setHead(fileName, response, isAttach);
        try (final OutputStream outputStream = response.getOutputStream()) {
            IoUtil.write(outputStream, false, bytes);
        }
    }

    /**
     * 设置相应头
     * @param fileName
     * @param response
     * @param isAttach
     * @throws UnsupportedEncodingException
     */
    private static void setHead(String fileName, HttpServletResponse response, boolean isAttach) throws UnsupportedEncodingException {
        if (isAttach) {
            response.setContentType("application/octet-stream; charset=UTF-8");
        } else {
            response.setHeader("Cache-Control", "max-age=86400");
            final String suffix = FileUtil.getSuffix(fileName);
            if (StringUtils.isNotEmpty(suffix)) {
                response.setContentType(MimeMappings.DEFAULT.get(suffix.trim().toLowerCase()));
            }
        }
        final StringBuilder contentDisposition = new StringBuilder();
        if (isAttach) {
            contentDisposition.append("attachment;");
        }
        contentDisposition.append("filename=").append(URLEncoder.encode(fileName, "UTF-8"));
        response.setHeader("Content-Disposition", contentDisposition.toString());
    }
}
