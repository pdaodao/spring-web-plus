package com.github.pdaodao.springwebplus.tool.db.dialect.mysql;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.IndexInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDDLGen;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.TableInfo;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;

public class MysqlDDLGen extends BaseDDLGen {

    public MysqlDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }


    @Override
    public String genDDLOfCreateIndex(TableInfo tableInfo, IndexInfo indexInfo) {
        return StrUtil.format("CREATE {} INDEX {} ON {} ({})",
                false == indexInfo.isNonUnique() ? "UNIQUE" : "",
                indexInfo.getIndexName(),
                getFullTableName(tableInfo),
                indexInfoColumns(indexInfo));
    }

    @Override
    protected String genDDLTableComment(TableInfo tableInfo, DDLBuildContext ddlBuildContext) {
        final StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(tableInfo.getComment())) {
            sb.append("\nCOMMENT '").append(StrUtils.clean(tableInfo.getComment(), 60)).append("'");
        }
        return sb.toString();
    }
}
