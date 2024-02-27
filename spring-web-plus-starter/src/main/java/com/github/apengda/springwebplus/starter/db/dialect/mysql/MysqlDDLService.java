package com.github.apengda.springwebplus.starter.db.dialect.mysql;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.meta.IndexInfo;
import com.github.apengda.springwebplus.starter.db.dialect.DataTypeConverter;
import com.github.apengda.springwebplus.starter.db.dialect.base.BaseDDLService;
import com.github.apengda.springwebplus.starter.db.pojo.DDLBuildContext;
import com.github.apengda.springwebplus.starter.db.pojo.TableInfo;
import com.github.apengda.springwebplus.starter.util.StrUtils;

public class MysqlDDLService extends BaseDDLService {
    public MysqlDDLService(DataTypeConverter dataTypeConverter) {
        super(dataTypeConverter);
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
            sb.append("\nCOMMENT '").append(StrUtils.cleanComment(tableInfo.getComment())).append("'");
        }
        return sb.toString();
    }
}
