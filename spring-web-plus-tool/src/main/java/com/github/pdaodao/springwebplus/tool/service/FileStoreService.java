package com.github.pdaodao.springwebplus.tool.service;

import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;

import java.io.IOException;
import java.io.InputStream;

public interface FileStoreService {

    /**
     * 文件是否存在
     *
     * @param fullPath 文件全路径
     * @return
     */
    Boolean exist(String fullPath);

    /**
     * 上传文件
     *
     * @param basePath    相对地址
     * @param fileName    文件名称
     * @param inputStream 输入文件流
     * @return
     * @throws IOException
     */
    String upload(String basePath, String fileName, InputStream inputStream) throws IOException;


    /**
     * 下载文件
     *
     * @param fullPath 文件唯一标识(全路径)
     * @return 输出流
     * @throws IOException 发生IO异常时抛出
     */
    InputStreamWrap download(final String fullPath) throws IOException;

    /**
     * 删除文件
     *
     * @param fullPath 文件唯一标识(全路径)
     * @return 是否删除成功
     * @throws IOException                   发生IO异常时抛出
     * @throws UnsupportedOperationException 当目标文件系统不支持删除文件操作时抛出
     */
    boolean delete(String fullPath) throws IOException, UnsupportedOperationException;
}
