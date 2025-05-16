package com.github.pdaodao.springwebplus.tool.fs.local;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    public Boolean exist(String relativePath) {
        return FileUtil.exist(fullPath(relativePath));
    }

    @Override
    public String fileSep() {
        return File.separator;
    }

    @Override
    public String fullPath(String path) {
        return pathAddRoot(config.getRootPath(), path);
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
    public InputStreamWrap download(final String relativePath) throws IOException {
        final String path = pathAddRoot(config.getRootPath(), relativePath);
        return InputStreamWrap.of(FileUtil.getInputStream(path));
    }

    @Override
    public boolean delete(final String relativePath) throws IOException, UnsupportedOperationException {
        final String path = pathAddRoot(config.getRootPath(), relativePath);
        return FileUtil.del(path);
    }

    @Override
    public List<FileInfo> listFiles(String path) {
        final String fullPath = pathAddRoot(config.getRootPath(), path);
        final List<File> files = FileUtil.loopFiles(FileUtil.file(fullPath), 1, null);
        if(CollUtil.isEmpty(files)){
            return null;
        }
        final List<FileInfo> ret = new ArrayList<>();
        for(final File f: files){
            final FileInfo fileInfo = new FileInfo();
            fileInfo.setName(f.getName());
            fileInfo.setPath(FilePathUtil.pathJoin(path, f.getName()));
            fileInfo.setIsDir(f.isDirectory());
            ret.add(fileInfo);
        }
        return ret;
    }
}
