package com.github.pdaodao.springwebplus.tool.db.dialect.sqlite;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDDLGen;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
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
    public List<String> alterColumnSql(TableColumn from, TableColumn to, DDLBuildContext ddlBuildContext) {
        final List<String> sqls = super.alterColumnSql(from, to, ddlBuildContext);
        log.error("unsupport alter column[{}]", StrUtil.join(";", sqls));
        return null;
    }
}
