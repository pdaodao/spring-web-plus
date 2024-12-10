package com.github.pdaodao.springwebplus.tool.fs;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class FileInfo {
    private String id;
    private String name;
    private String path;
    private String contentType;
    private Long size;
    private transient String readableSize;

    protected Date createTime;

    private String namespace;

    private String pid;

    public String getReadableSize() {
        if (Objects.isNull(size)) {
            return "0";
        }
        return FileUtil.readableFileSize(size);
    }
}