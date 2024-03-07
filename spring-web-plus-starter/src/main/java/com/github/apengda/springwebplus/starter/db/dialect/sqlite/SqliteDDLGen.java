package com.github.apengda.springwebplus.starter.db.dialect.sqlite;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.starter.db.dialect.DbDialect;
import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDDLGen;
import com.github.apengda.springwebplus.starter.db.pojo.ColumnInfo;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SqliteDDLGen extends BaseDDLGen {

    public SqliteDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }

    @Override
    protected String genDDLTableComment(TableInfo tableInfo, DDLBuildContext ddlBuildContext) {
        return null;
    }

    @Override
    public List<String> alterColumnSql(ColumnInfo from, ColumnInfo to, DDLBuildContext ddlBuildContext) {
        final List<String> sqls  = super.alterColumnSql(from, to, ddlBuildContext);
        log.error("unsupport alter column[{}]", StrUtil.join(";", sqls));
        return null;
    }
}
