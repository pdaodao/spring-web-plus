package com.github.apengda.springwebplus.starter.db.util;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.starter.db.dialect.DbDialect;
import com.github.apengda.springwebplus.starter.db.dialect.DbFactory;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class DDLUtil {

    /**
     * 扫描实体结构信息和数据库表结构信息进行对比
     *
     * @param ds
     */
    public static void dbCheck(final DataSource ds) {
        final List<TableInfo> entityList = DbEntityUtil.entityList();
        final List<TableInfo> tables = DbMetaUtil.tableInfoList(ds);
        final Map<String, TableInfo> oldMap = new HashMap<>();
        tables.forEach(t -> oldMap.put(t.getTableName(), t));

        final DbDialect dbDialect = DbFactory.of(ds);

        for (final TableInfo info : entityList) {
            tableCheck(dbDialect, ds, info, oldMap.get(info.getTableName()));
        }
    }

    public static void tableCheck(final DbDialect dbDialect, final DataSource ds, final TableInfo tableInfo, final TableInfo old) {
        final List<String> sqls = genDDL(dbDialect, tableInfo, old);
        log.info("------------- " + tableInfo.getTableName() + "---------");
        log.info(StrUtil.join(";\n", sqls));
        try {
            DbUtil.executeSqlBlock(ds, sqls);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static List<String> genDDL(final DbDialect dbDialect, final TableInfo tableInfo, final TableInfo old) {
        if (old == null) {
            return dbDialect.ddlGen().createTable(tableInfo);
        }
        return null;
    }

}
