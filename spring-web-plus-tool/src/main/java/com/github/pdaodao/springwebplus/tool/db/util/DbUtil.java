package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.sql.SqlExecutor;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlList;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

@Slf4j
public class DbUtil {

    public static void executeSqlBlock(final DataSource dataSource, final List<String> list) throws Exception {
        executeSqlBlock(dataSource, SqlList.of(list));
    }

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
        final Connection connection = dataSource.getConnection();
        final boolean auto = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            if (StrUtil.isNotBlank(sqlList.getSelectSql())) {
                try (final ResultSet rs = connection.prepareStatement(sqlList.getSelectSql()).executeQuery()) {
                    if (rs.next()) {
                        return;
                    }
                }
            }
            for (final String sql : sqlList.getSqls()) {
                SqlExecutor.execute(connection, sql);
            }
            connection.commit();
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(auto);
            connection.close();
        }
    }
}
