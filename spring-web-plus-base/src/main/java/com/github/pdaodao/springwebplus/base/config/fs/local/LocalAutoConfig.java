package com.github.pdaodao.springwebplus.base.config.fs.local;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalFileStorage;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class LocalAutoConfig {

    @Bean
    @ConditionalOnMissingBean(FileStorage.class)
    public FileStorage fileSystem(final LocalFsConfig localConfig) {
        if (StrUtil.isBlank(localConfig.getRootPath())) {
            localConfig.setRootPath(FilePathUtil.pathJoin(System.getProperty("user.dir"), "upload"));
        }
        final LocalFileStorage fileSystem = new LocalFileStorage(localConfig);
        fileSystem.init();
        return fileSystem;
    }
}