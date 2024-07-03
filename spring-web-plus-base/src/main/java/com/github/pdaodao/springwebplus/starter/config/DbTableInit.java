package com.github.pdaodao.springwebplus.starter.config;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.starter.db.pojo.SqlList;
import com.github.pdaodao.springwebplus.starter.db.util.DDLUtil;
import com.github.pdaodao.springwebplus.starter.db.util.DbUtil;
import com.github.pdaodao.springwebplus.starter.db.util.SqlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * 表结构初始化
 */
@Service
@Slf4j
@AllArgsConstructor
public class DbTableInit implements CommandLineRunner, Ordered {
    private final DataSource dataSource;
    private final SysConfigProperties configProperties;

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void run(String... args) throws Exception {
        //1. 转表结构
        entityToTable();
        //2. 初始化sql
        sqlInit();
    }

    /**
     * 实体自动转为表结构
     */
    private void entityToTable() {
        if(false == configProperties.getDdlGenEnabled()){
            return;
        }
        DDLUtil.dbCheck(dataSource, configProperties.getDdlGenDeleteField());
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
