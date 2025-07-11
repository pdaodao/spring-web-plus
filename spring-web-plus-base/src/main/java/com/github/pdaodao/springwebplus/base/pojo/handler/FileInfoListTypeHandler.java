package com.github.pdaodao.springwebplus.base.pojo.handler;

import com.github.pdaodao.springwebplus.base.frame.JsonListTypeHandler;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;

public class FileInfoListTypeHandler extends JsonListTypeHandler<FileInfo> {
    public FileInfoListTypeHandler(Class<FileInfo> clazz) {
        super(clazz);
    }
}
