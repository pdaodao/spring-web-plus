package com.github.pdaodao.springwebplus.fs.local;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class LocalAutoConfig {

    @Bean
    @ConditionalOnMissingBean(FileStorage.class)
    public FileStorage fileSystem(final LocalConfig localConfig) {
        if(StrUtil.isBlank(localConfig.rootPath)){
            localConfig.rootPath = FilePathUtil.pathJoin(System.getProperty("user.dir"), "upload");
        }
        final LocalFileStorage fileSystem = new LocalFileStorage(localConfig);
        fileSystem.init();
        return fileSystem;
    }
}