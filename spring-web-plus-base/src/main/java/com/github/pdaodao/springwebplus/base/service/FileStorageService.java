package com.github.pdaodao.springwebplus.base.service;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.service.FileStoreService;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件存取服务
 */
@Service
@AllArgsConstructor
public class FileStorageService implements FileStoreService {
    private final FileStorage fileStorage;

    /**
     * 文件是否存在
     *
     * @param fullPath 文件全路径
     * @return
     */
    public Boolean exist(final String fullPath) {
        return fileStorage.exist(fullPath);
    }

    /**
     * 上传文件
     *
     * @param basePath    相对路径
     * @param fileSize    文件大小
     * @param fileName    文件名称
     * @param inputStream 文件流
     * @return
     * @throws IOException
     */
    public String upload(final String basePath, final Long fileSize, final String fileName,
                         final InputStream inputStream) throws IOException {
        return fileStorage.upload(processBasePath(basePath), fileSize, fileName, inputStream);
    }

    protected String processBasePath(String basePath){
        if(StrUtil.isBlank(basePath)){
            basePath = "";
        }else{
            basePath = FileNameUtil.cleanInvalid(basePath);
        }
        return fileStorage.pathJoin(basePath, DateTimeUtil.formatYearMonth(DateTimeUtil.now()));
    }

    /**
     * 上传文件
     *
     * @param basePath    相对路径
     * @param fileName    文件名称
     * @param inputStream 文件流
     * @return
     * @throws IOException
     */
    public String upload(final String basePath, final String fileName, final InputStream inputStream) throws IOException {
        return fileStorage.upload(processBasePath(basePath), fileName, inputStream);
    }

    /**
     * 上传文件
     *
     * @param basePath
     * @param file
     * @return
     * @throws IOException
     */
    public FileInfo upload(final String basePath, final MultipartFile file) throws Exception {
        final String fileName = FileUtil.cleanInvalid(file.getOriginalFilename());
        try (final InputStream inputStream = file.getInputStream()) {
            final FileInfo fileInfo = new FileInfo();
            fileInfo.setName(fileName);
            fileInfo.setContentType(FileTypeUtil.getType(null, file.getOriginalFilename(), true));
            final String fullPath = upload(basePath, file.getSize(), fileName, inputStream);
            fileInfo.setPath(fullPath);
            fileInfo.setSize(file.getSize());
            return fileInfo;
        }
    }

    /**
     * 下载文件
     *
     * @param fullPath 文件唯一标识(全路径)
     * @return 输出流
     * @throws IOException 发生IO异常时抛出
     */
    public InputStreamWrap download(final String fullPath) throws IOException {
        return fileStorage.download(fullPath);
    }

    /**
     * 删除文件
     *
     * @param fullPath 文件唯一标识(全路径)
     * @return 是否删除成功
     * @throws IOException                   发生IO异常时抛出
     * @throws UnsupportedOperationException 当目标文件系统不支持删除文件操作时抛出
     */
    public boolean delete(final String fullPath) throws IOException, UnsupportedOperationException {
        return fileStorage.delete(fullPath);
    }
}
