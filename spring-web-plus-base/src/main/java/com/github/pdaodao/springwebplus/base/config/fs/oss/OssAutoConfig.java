package com.github.pdaodao.springwebplus.base.config.fs.oss;

import com.github.pdaodao.springwebplus.base.config.fs.local.LocalAutoConfig;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.oss.OssFileSystem;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfigureBefore(LocalAutoConfig.class)
@ConditionalOnProperty(name = "spring.fs", havingValue = "oss")
public class OssAutoConfig {

    @Bean
    public FileStorage fileSystem(OssFsConfig ossConfig) {
        OssFileSystem ossFileSystem = new OssFileSystem(ossConfig);
        ossFileSystem.init();
        return ossFileSystem;
    }
}
