package com.github.apengda.springwebplus.starter.db;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.github.apengda.springwebplus.starter.db.pojo.SqlList;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import com.github.apengda.springwebplus.starter.db.util.DbMetaUtil;
import com.github.apengda.springwebplus.starter.db.util.SqlUtil;
import com.github.apengda.springwebplus.starter.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
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
                executeSqlBlock(block);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void executeSqlBlock(final SqlList sqlList) throws Exception {
        if (sqlList == null || sqlList.isEmpty()) {
            return;
        }
        boolean next = true;
        final Connection connection = dataSource.getConnection();
        final boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            if (StrUtil.isNotBlank(sqlList.getSelectSql())) {
            }
            for (final String sql : sqlList.getSqls()) {
                log.info(sql);
                SqlExecutor.execute(connection, sql);
            }
            connection.commit();
            ;
        } catch (Exception e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(auto);
            connection.close();
        }
    }


}
