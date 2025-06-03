package com.github.pdaodao.springwebplus.tool.db.dialect.starrocks;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.table.TableIndex;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDDLGen;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StarRocksDDLGen extends MysqlDDLGen {
    public StarRocksDDLGen(DbDialect dbDialect) {
        super(dbDialect);
    }

    @Override
    public String genDDLOfPk(TableInfo tableInfo, DDLBuildContext context) {
        return null;
    }

    @Override
    public String genDDLOfCreateIndex(TableInfo tableInfo, TableIndex indexInfo) {
        return null;
    }

    @Override
    protected String genDDLOfPartition(TableInfo tableInfo, DDLBuildContext context) {
        if(tableInfo.getTablePartition() == null
                || StrUtil.isBlank(tableInfo.getTablePartition().getName())){
            return null;
        }
        return StrUtil.format("PARTITION BY date_trunc('{}', {})",
                tableInfo.getTablePartition().getTimeUnit().name(),
                tableInfo.getTablePartition().getName());
    }

    @Override
    public String genDDLOfPkTable(TableInfo tableInfo, DDLBuildContext context) {
        final List<String> pks = tableInfo.pkColumnNames();
        if(tableInfo.getBucketNum() == null){
            tableInfo.setBucketNum(1);
        }
        if(CollUtil.isNotEmpty(pks)){
            final String f = pks.stream().map(t -> dbDialect.quoteIdentifier(t)).collect(Collectors.joining(","));
            return StrUtil.format("PRIMARY KEY ({}) \nDISTRIBUTED BY HASH ({}) BUCKETS {} ", f, f, tableInfo.getBucketNum());
        }
        return StrUtil.format("DISTRIBUTED BY HASH ({}) BUCKETS {} ",
                dbDialect.quoteIdentifier(tableInfo.getFields().get(0).getName()), tableInfo.getBucketNum());
    }

    @Override
    protected String genDDLTableEngineInfo(TableInfo tableInfo, DDLBuildContext context) {
        if(tableInfo.getReplicationNum() == null){
            tableInfo.setReplicationNum(1);
        }
        final List<String> pList = new ArrayList<>();
        pList.add("\"replication_num\"="+tableInfo.getReplicationNum());
        return StrUtil.format("PROPERTIES (\n{}\n) ", StrUtil.join(",\n", pList));
    }
}
