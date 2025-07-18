package com.github.pdaodao.springwebplus.tool.nosql;

import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import javax.annotation.Nullable;


public interface NosqlQueryExecutor {


    /**
     * 执行nosql语句查询
     * @param dbInfo      数据源信息
     * @param bound       最大返回数据行数 防止返回数据过多导致内存溢出 null 表示不控制
     * @param pageInfo    分页
     * @param sql         sql语句
     * @param args        语句中的参数
     * @return
     * @throws Exception
     */
    TableData execute(DbInfo dbInfo, Integer bound, PageInfo pageInfo, String sql, @Nullable Object... args) throws Exception;
}
