package com.github.pdaodao.springwebplus.base.config.fs.minio;

import com.github.pdaodao.springwebplus.tool.fs.minio.MinioConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * minio 连接信息
 */
@Configuration
@ConfigurationProperties(prefix = "fs.minio")
public class MinioFsConfig extends MinioConfig {

}