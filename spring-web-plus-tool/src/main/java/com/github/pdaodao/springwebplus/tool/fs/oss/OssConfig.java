package com.github.pdaodao.springwebplus.tool.fs.oss;

import lombok.Data;

/**
 * oss 连接信息配置
 */
@Data
public class OssConfig {
    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint;

    private String bucketName;

    private String rootPath;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public OssConfig setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
        return this;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public OssConfig setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
        return this;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public OssConfig setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public OssConfig setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getRootPath() {
        return rootPath;
    }

    public OssConfig setRootPath(String rootPath) {
        this.rootPath = rootPath;
        return this;
    }
}
