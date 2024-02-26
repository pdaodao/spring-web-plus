package com.github.apengda.springwebplus.starter.db;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.MetaUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@AllArgsConstructor
public class DbInit implements CommandLineRunner {
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        ThreadUtil.sleep(1000);
        System.out.println("haha");
        final List<String> tbs = MetaUtil.getTables(dataSource);
        System.out.println(StrUtil.repeat("-", 50));
        for (final String t : tbs) {
            System.out.println(t);
        }
    }
}
