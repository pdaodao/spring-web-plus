package com.github.pdaodao.springwebplus.tool.fs.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSBuilder;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.BucketInfo;
import com.aliyun.oss.model.OSSObject;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class OssFileSystem implements FileStorage {
    private static final Logger logger = LoggerFactory.getLogger(OssFileSystem.class);
    private final OssConfig ossConfig;
    private transient OSS oss;

    public OssFileSystem(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
        Preconditions.checkNotEmpty(ossConfig.getAccessKeyId(), "未配置阿里云oss的AccessKeyId");
        Preconditions.checkNotEmpty(ossConfig.getAccessKeySecret(), "未配置阿里云oss的AccessKeySecret");
        Preconditions.checkNotEmpty(ossConfig.getEndpoint(), "未配置阿里云oss的Endpoint");
        Preconditions.checkNotEmpty(ossConfig.getBucketName(), "未配置阿里云oss的BucketName");
    }

    /**
     * 初始化连接
     */
    public void init() {
        final OSSBuilder ossBuilder = new OSSClientBuilder();
        oss = ossBuilder.build(ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());
        if (!oss.doesBucketExist(ossConfig.getBucketName())) {
            oss.createBucket(ossConfig.getBucketName());
        }
        final BucketInfo bucketInfo = oss.getBucketInfo(ossConfig.getBucketName());
        logger.info("connect to oss success. {}", ossConfig.getEndpoint());
    }

    @Override
    public Boolean exist(final String fullPath) {
        final String path = pathAddRoot(ossConfig.getRootPath(), fullPath);
        return oss.doesObjectExist(ossConfig.getBucketName(), path);
    }

    @Override
    public String upload(final String basePath, Long fileSize, String fileName, InputStream inputStream) throws IOException {
        final String fullPath = concatPathForSave(ossConfig.getRootPath(), basePath, fileName);
        oss.putObject(ossConfig.getBucketName(), fullPath, inputStream);
        return FilePathUtil.dropRootPath(fullPath, ossConfig.getRootPath());
    }

    @Override
    public InputStreamWrap download(String fullPath) throws IOException {
        final String path = pathAddRoot(ossConfig.getRootPath(), fullPath);
        final OSSObject ossObject = oss.getObject(ossConfig.getBucketName(), path);
        return InputStreamWrap.of(ossObject.getObjectContent());
    }

    @Override
    public boolean delete(String fullPath) throws IOException, UnsupportedOperationException {
        final String path = pathAddRoot(ossConfig.getRootPath(), fullPath);
        oss.deleteObject(ossConfig.getBucketName(), path);
        return true;
    }

    @Override
    public String processPathForSave(String fullPath) {
        return fullPath.startsWith("/")
                ? fullPath.substring(1) : fullPath;
    }
}
