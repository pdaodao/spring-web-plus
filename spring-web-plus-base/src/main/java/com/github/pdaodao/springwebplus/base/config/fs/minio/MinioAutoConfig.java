package com.github.pdaodao.springwebplus.base.config.fs.minio;

import com.github.pdaodao.springwebplus.base.config.fs.local.LocalAutoConfig;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.minio.MinioFileStorage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfigureBefore(LocalAutoConfig.class)
@ConditionalOnProperty(name = "spring.fs", havingValue = "minio")
public class MinioAutoConfig {

    @Bean
    public FileStorage fileSystemMinio(MinioFsConfig minioConfig) throws Exception {
        MinioFileStorage fileSystem = new MinioFileStorage(minioConfig);
        fileSystem.init();
        return fileSystem;
    }
}