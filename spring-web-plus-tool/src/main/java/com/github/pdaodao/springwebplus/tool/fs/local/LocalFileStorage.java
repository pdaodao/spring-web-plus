package com.github.pdaodao.springwebplus.tool.fs.local;

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
        Preconditions.checkNotBlank(config.getRootPath(), "file local storage root-path is empty.");
    }

    public void init() {
        if (!FileUtil.exist(config.getRootPath())) {
            FileUtil.mkdir(config.getRootPath());
        }
    }

    @Override
    public Boolean exist(String fullPath) {
        return FileUtil.exist(pathAddRoot(config.getRootPath(), fullPath));
    }

    @Override
    public String upload(String basePath, Long fileSize, String fileName, InputStream inputStream) throws IOException {
        if (StrUtil.isNotBlank(basePath)) {
            String path = FilePathUtil.pathJoin(config.getRootPath(), basePath);
            if (!FileUtil.exist(path)) {
                FileUtil.mkdir(path);
            }
        }
        final String fullPath = concatPathForSave(config.getRootPath(), basePath, fileName);
        FileUtil.writeFromStream(inputStream, fullPath);
        return FilePathUtil.dropRootPath(fullPath, config.getRootPath());
    }

    @Override
    public InputStreamWrap download(String fullPath) throws IOException {
        final String path = pathAddRoot(config.getRootPath(), fullPath);
        return InputStreamWrap.of(FileUtil.getInputStream(path));
    }

    @Override
    public boolean delete(String fullPath) throws IOException, UnsupportedOperationException {
        final String path = pathAddRoot(config.getRootPath(), fullPath);
        return FileUtil.del(path);
    }
}
