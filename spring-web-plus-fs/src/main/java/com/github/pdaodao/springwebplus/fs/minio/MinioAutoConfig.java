package com.github.pdaodao.springwebplus.fs.minio;

import com.github.pdaodao.springwebplus.fs.local.LocalAutoConfig;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfigureBefore(LocalAutoConfig.class)
@ConditionalOnProperty(name = "spring.fs", havingValue = "minio")
public class MinioAutoConfig {

    @Bean
    public FileStorage fileSystemMinio(MinioConfig minioConfig) throws Exception{
        MinioFileStorage fileSystem = new MinioFileStorage(minioConfig);
        fileSystem.init();
        return fileSystem;
    }
}