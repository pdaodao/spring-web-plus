package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.util.BooleanUtil;
import com.github.pdaodao.springwebplus.base.frame.EntityScanUtil;
import com.github.pdaodao.springwebplus.base.util.SqlInitUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.db.util.DBDdLUtil;
import com.github.pdaodao.springwebplus.tool.db.util.DbMetaUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

/**
 * 表结构初始化
 */
@Service
@Slf4j
@AllArgsConstructor
public class EntityTableAutoInitCmd implements CommandLineRunner, Ordered {
    private final DataSource dataSource;
    private final SysConfigProperties configProperties;

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            //1. 执行前置 sql 语句
            SqlInitUtil.dbSqlInit("before", dataSource);
            //2. 表结构初始化
            entityToTable();
            //3. 执行后置 sql 语句
            SqlInitUtil.dbSqlInit("", dataSource);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 实体自动转为表结构
     */
    private void entityToTable() throws Exception {
        if (BooleanUtil.isFalse(configProperties.getDdlGenEnabled())) {
            return;
        }
        final DbDialect dbDialect = DbFactory.of(configProperties.getDatasourceUrl());
        String dbSchema;
        try (final Connection cn = dataSource.getConnection()) {
            dbSchema = cn.getSchema();
        }
        final List<TableInfo> tableInfoList = EntityScanUtil.entityList();
        for (final TableInfo tableInfo : tableInfoList) {
            final TableInfo old = DbMetaUtil.tableInfo(dataSource, tableInfo.getName(), dbSchema, dbDialect);
            DBDdLUtil.tableCheck(dbDialect, configProperties.getDdlGenDeleteField(), dataSource, tableInfo, old);
        }
    }
}
