package com.github.pdaodao.springwebplus.tool.fs.minio;

import lombok.Data;

/**
 * minio 连接信息
 */
@Data
public class MinioConfig {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucketName;

    private String rootPath;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public MinioConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public MinioConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public MinioConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public MinioConfig setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public MinioConfig setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}