package com.github.pdaodao.springwebplus.base.frame;

import com.github.pdaodao.springwebplus.tool.fs.FileInfo;

public class FileInfoListJsonHandler extends JsonListTypeHandler<FileInfo> {
    public FileInfoListJsonHandler(Class<FileInfo> clazz) {
        super(clazz);
    }
}
