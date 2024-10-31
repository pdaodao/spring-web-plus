package com.github.pdaodao.springwebplus.fs.local;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * 本地文件存储
 */
public class LocalFileStorage implements FileStorage {
    private final LocalConfig config;

    public LocalFileStorage(LocalConfig config) {
        this.config = config;
        Preconditions.checkNotBlank(config.rootPath, "file local storage root-path is empty.");
    }

    public void init() {
        if (!FileUtil.exist(config.rootPath)) {
            FileUtil.mkdir(config.rootPath);
        }
    }

    @Override
    public Boolean exist(String fullPath) {
        return FileUtil.exist(fullPath);
    }

    @Override
    public String upload(String basePath, Long fileSize, String fileName, InputStream inputStream) throws IOException {
        if (StrUtil.isNotBlank(basePath)) {
            String path = FilePathUtil.pathJoin(config.rootPath, basePath);
            if (!FileUtil.exist(path)) {
                FileUtil.mkdir(path);
            }
        }
        final String fullPath = concatPathForSave(config.rootPath, basePath, fileName);
        FileUtil.writeFromStream(inputStream, fullPath);
        return fullPath;
    }

    @Override
    public InputStreamWrap download(String fullPath) throws IOException {
        return InputStreamWrap.of(FileUtil.getInputStream(fullPath));
    }

    @Override
    public boolean delete(String fullPath) throws IOException, UnsupportedOperationException {
        return FileUtil.del(fullPath);
    }
}
