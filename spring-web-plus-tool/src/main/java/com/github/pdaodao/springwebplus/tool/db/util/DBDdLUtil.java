package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.map.CaseInsensitiveLinkedMap;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.SqlType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.SqlCmd;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;

@Slf4j
public class DBDdLUtil {
    /**
     * 检查数据表结构变化
     *
     * @param dbDialect
     * @param isDeleteField
     * @param ds
     * @param tableInfo
     * @param old
     */
    public static void tableCheck(final DbDialect dbDialect,
                                  final boolean isDeleteField,
                                  final DataSource ds, final TableInfo tableInfo, final TableInfo old) {
        final List<String> sqls = genDDL(dbDialect, isDeleteField, tableInfo, old);
        log.info("------------- " + tableInfo.getName() + "---------");
        log.info(StrUtil.join(";\n", sqls));
        try {
            DbUtil.executeSqlBlock(ds, sqls);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        autoIdRestart(dbDialect, ds, tableInfo);
    }

    public static String autoIdRestart(final DbDialect dbDialect, final DataSource ds, final TableInfo tableInfo) {
        final TableColumn autoIdColumn = tableInfo.autoIdColumn();
        if(autoIdColumn != null){
            final String restartSql = dbDialect.setAutoIdStartSql(tableInfo.getDbSchema(), tableInfo.getName(), autoIdColumn.getName());
            try{
                final SqlCmd cmd = new SqlCmd();
                cmd.setSqlType(SqlType.SELECT);
                cmd.setSql(restartSql);
                DbUtil.executeSqlBlock(ds, cmd);
                return restartSql;
            }catch (Exception e){
                log.error(restartSql);
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 对比老旧数据表结构 生成表结构变更语句
     *
     * @param dbDialect
     * @param isDeleteField
     * @param tableInfo
     * @param old
     * @return
     */
    public static List<String> genDDL(final DbDialect dbDialect,
                                      final boolean isDeleteField,
                                      final TableInfo tableInfo, final TableInfo old) {
        // 新建表
        if (old == null) {
            return dbDialect.ddlGen().createTable(tableInfo);
        }
        // 对比上次表结构同步后的变化
        final CaseInsensitiveLinkedMap<String, TableColumn> fieldMap = new CaseInsensitiveLinkedMap();
        final CaseInsensitiveLinkedMap<String, TableColumn> oldFieldMap = new CaseInsensitiveLinkedMap();

        final DDLBuildContext ddlBuildContext = new DDLBuildContext(tableInfo.getName());
        for (final TableColumn f : tableInfo.getColumns()) {
            if(f.getDataType() == DataType.ARRAY || f.getDataType() == DataType.MAP){
                if(f.getLength() >= 3000){
                    f.setDataType(DataType.TEXT);
                }else{
                    f.setDataType(DataType.STRING);
                }
            }
            fieldMap.put(f.getName(), f);
        }
        for (final TableColumn f : old.getColumns()) {
            oldFieldMap.put(f.getName(), f);
            // 删除旧的已经不用的字段
            if (!fieldMap.containsKey(f.getName())) {
                if (true == isDeleteField) {
                    final List<String> dSqls = dbDialect.ddlGen().dropColumnSql(tableInfo.getName(), f.getName());
                    ddlBuildContext.addSql(dSqls);
                }
                continue;
            }
            // 字段结构发生变化 更新
            final TableColumn toUpdated = fieldMap.get(f.getName());
            if (f.diff(toUpdated, false)) {
                final List<String> alterSqls = dbDialect.ddlGen().alterColumnSql(f, toUpdated, ddlBuildContext);
                ddlBuildContext.addSql(alterSqls);
            }
        }
        // 新增字段
        for (final TableColumn f : tableInfo.getColumns()) {
            if (!oldFieldMap.containsKey(f.getName())) {
                ddlBuildContext.addSql(dbDialect.ddlGen().addColumnSql(f, ddlBuildContext));
            }
        }
        ddlBuildContext.addSql(ddlBuildContext.lastSql);
        return ddlBuildContext.sqls;
    }
}
