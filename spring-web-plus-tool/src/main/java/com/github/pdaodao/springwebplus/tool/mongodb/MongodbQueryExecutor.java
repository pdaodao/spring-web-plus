package com.github.pdaodao.springwebplus.tool.mongodb;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.nosql.BaseNosqlQueryExecutor;
import com.github.pdaodao.springwebplus.tool.nosql.NosqlQueryExecutor;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongodbQueryExecutor extends BaseNosqlQueryExecutor implements NosqlQueryExecutor {

    @Override
    public TableData doExecute(final DbInfo dbInfo, final Integer bound, final PageInfo pageInfo, final String sql) throws Exception {
        if (dbInfo == null || StrUtil.isBlank(sql)) {
            return null;
        }
        final MongoClient client = MongodbUtil.getClient(dbInfo);
        MongoDatabase mongoDatabase = client.getDatabase(dbInfo.getDbName());
        return MongodbSqlUtil.execute(mongoDatabase, sql, pageInfo);
    }
}
