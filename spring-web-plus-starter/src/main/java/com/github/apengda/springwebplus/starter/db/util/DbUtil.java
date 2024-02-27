package com.github.apengda.springwebplus.starter.db.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.github.apengda.springwebplus.starter.db.pojo.SqlList;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
public class DbUtil {

    /**
     * 在一个事务中执行多条sql语句
     *
     * @param dataSource
     * @param sqlList
     * @throws Exception
     */
    public static void executeSqlBlock(final DataSource dataSource, final SqlList sqlList) throws Exception {
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
        } catch (Exception e) {
            connection.rollback();
        } finally {
            connection.setAutoCommit(auto);
            connection.close();
        }
    }
}
