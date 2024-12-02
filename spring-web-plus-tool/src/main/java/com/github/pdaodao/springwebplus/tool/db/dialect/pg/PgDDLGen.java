package com.github.pdaodao.springwebplus.tool.db.dialect.pg;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableIndex;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.base.BaseDDLGen;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;

public class PgDDLGen extends BaseDDLGen {

    public PgDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }

    /**
     * 判断索引中是否含有部分表名称 如果没有则索引名称要加上表名称
     *
     * @return true: 表示不需要加上表名
     */
    public static boolean hasPartName(final String indexName, final String tableName) {
        if (StrUtil.isBlank(tableName) || StrUtil.isBlank(indexName)) {
            return true;
        }
        if (StrUtil.containsIgnoreCase(indexName, tableName)) {
            return true;
        }
        double d = StrUtil.similar(indexName.toLowerCase(), tableName.toLowerCase());
        return d > 0.7;
    }

    public static String genIndexName(String indexName, String tableName) {
        Preconditions.checkNotEmpty(indexName, "indexName cannot empty");
        if (hasPartName(indexName, tableName)) {
            return indexName;
        }
        return tableName + indexName;
    }

    @Override
    public String genDDLOfCreateIndex(TableInfo tableInfo, TableIndex indexInfo) {
        return StrUtil.format("CREATE {} INDEX {} ON {} ({})",
                false == indexInfo.getIsUnique() ? "UNIQUE" : "",
                indexInfo.getName(),
                getFullTableName(tableInfo),
                indexInfoColumns(indexInfo));
    }

    @Override
    protected String genDDLTableComment(TableInfo tableInfo, DDLBuildContext ddlBuildContext) {
        final String sql = StrUtil.format("COMMENT ON TABLE {} IS '{}'", ddlBuildContext.tableName, StrUtils.clean(tableInfo.getRemark()));
        ddlBuildContext.addLastSql(sql);
        return null;
    }
}
