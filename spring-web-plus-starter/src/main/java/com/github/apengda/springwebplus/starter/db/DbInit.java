package com.github.apengda.springwebplus.starter.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.github.apengda.springwebplus.starter.db.pojo.SqlList;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import com.github.apengda.springwebplus.starter.db.util.DbMetaUtil;
import com.github.apengda.springwebplus.starter.db.util.DbUtil;
import com.github.apengda.springwebplus.starter.db.util.SqlUtil;
import com.github.apengda.springwebplus.starter.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class DbInit implements CommandLineRunner {
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        ThreadUtil.sleep(1000);
        System.out.println("haha");
        //1. 转表结构
        entityToTable();
        //2. 初始化sql
        sqlInit();
    }

    /**
     * 实体自动转为表结构
     */
    private void entityToTable() {
        List<String> tables = DbMetaUtil.getTables(dataSource);
        for (final String t : tables) {
            System.out.println("-----===== ahha " + t);
            final TableInfo tableInfo = DbMetaUtil.getTableMeta(dataSource, t);
            System.out.println(JsonUtil.toJsonString(tableInfo));
        }
    }

    private void sqlInit() {
        final List<SqlList> sqlBlocks = SqlUtil.readToSqlBlock("sql-init.sql");
        if (CollUtil.isEmpty(sqlBlocks)) {
            return;
        }
        for (final SqlList block : sqlBlocks) {
            try {
                DbUtil.executeSqlBlock(dataSource, block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
