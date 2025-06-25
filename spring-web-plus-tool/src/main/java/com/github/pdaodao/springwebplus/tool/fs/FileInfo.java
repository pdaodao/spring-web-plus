package com.github.pdaodao.springwebplus.tool.fs;

import cn.hutool.core.io.FileUtil;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
public class FileInfo {
    private String id;
    private String namespace;
    private String name;
    private String path;

    private String contentType;
    private Long size;
    private Boolean isDir;
    private transient String readableSize;
    private String objId;
    protected Date createTime;

    public String getReadableSize() {
        if (Objects.isNull(size)) {
            return "0";
        }
        return FileUtil.readableFileSize(size);
    }

    public static FileInfo of(final String path){
        final FileInfo f = new FileInfo();
        f.setPath(path);
        return f;
    }
}