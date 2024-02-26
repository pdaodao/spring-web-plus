package com.github.apengda.springwebplus.starter.config;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.github.apengda.springwebplus.starter.util.IdUtil;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbSnowIdGenerator implements IdentifierGenerator {
    @Override
    public Number nextId(Object entity) {
        return null;
    }

    @Override
    public String nextUUID(Object entity) {
        return IdUtil.snowId();
    }
}
