package com.github.pdaodao.springwebplus.tool.fs;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public interface FileStorage {
    ThreadLocal<Boolean> isOverwrite = new ThreadLocal<>();

    static void setOverwrite(){
        isOverwrite.set(true);
    }

    static void clearHolder(){
        isOverwrite.remove();
    }

    /**
     * 全路径
     * @param path
     * @return
     */
    String fullPath(final String path);

    default String pathJoin(String... paths){
        return FilePathUtil.join(fileSep(), paths);
    }

    default String fileSep(){
        return "/";
    }

    /**
     * 文件保存时拼装全路径
     *
     * @param baseRootPath 根路径
     * @param relativePath 相对路径
     * @param fileName     文件名称
     * @return
     */
    default String concatPathForSave(final String baseRootPath, final String relativePath, String fileName) {
        Preconditions.checkNotEmpty(fileName, "文件名称不能为空");
        fileName = FileNameUtil.cleanInvalid(fileName);
        String fullPath = FilePathUtil.join(fileSep(), baseRootPath, relativePath, fileName);
        fullPath = processPathForSave(fullPath);
        if (exist(fullPath)) {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            for (int i = 0; i < 100; i++) {
                String name = sdf.format(new Date()) + i + fileName;
                if (i > 90) {
                    name = IdUtil.fastSimpleUUID() + fileName;
                }
                fullPath = FilePathUtil.pathJoin(baseRootPath, relativePath, name);
                fullPath = processPathForSave(fullPath);
                if (!exist(fullPath)) {
                    break;
                }
            }
        }
        return fullPath;
    }

    void init() throws Exception;

    default String processPathForSave(String fullPath) {
        return fullPath;
    }

    default String pathAddRoot(final String root, final String path) {
        final String p = FilePathUtil.pathJoin(root, path);
        return processPathForSave(p);
    }


    /**
     * 文件是否存在
     *
     * @param relativePath 文件相对路径
     * @return
     */
    Boolean exist(final String relativePath);


    /**
     * 上传文件
     *
     * @param basePath    相对地址
     * @param fileSize    文件大小
     * @param fileName    文件名称
     * @param inputStream 输入文件流
     * @return
     * @throws IOException
     */
    String upload(String basePath, Long fileSize, String fileName, InputStream inputStream) throws IOException;

    /**
     * 上传文件
     *
     * @param basePath    相对地址
     * @param fileName    文件名称
     * @param inputStream 输入文件流
     * @return
     * @throws IOException
     */
    default String upload(String basePath, String fileName, InputStream inputStream) throws IOException {
        return upload(basePath, Long.valueOf(inputStream.available()), fileName, inputStream);
    }

    /**
     * 下载文件
     *
     * @param relativePath  文件相对路径
     * @return 输出流
     * @throws IOException 发生IO异常时抛出
     */
    InputStreamWrap download(final String relativePath) throws IOException;

    /**
     * 删除文件
     *
     * @param relativePath    文件相对路径
     * @return 是否删除成功
     * @throws IOException                   发生IO异常时抛出
     * @throws UnsupportedOperationException 当目标文件系统不支持删除文件操作时抛出
     */
    boolean delete(final String relativePath) throws IOException, UnsupportedOperationException;

    /**
     * 列出路径下的目录和文件
     *
     * @param relativePath 文件相对路径
     * @return 目录和文件列表
     */
    default List<FileInfo> listFiles(String relativePath){
        return null;
    }
}
