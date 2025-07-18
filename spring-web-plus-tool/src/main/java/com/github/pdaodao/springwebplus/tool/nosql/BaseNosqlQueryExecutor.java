package com.github.pdaodao.springwebplus.tool.nosql;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.PageInfo;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;

public abstract class BaseNosqlQueryExecutor implements NosqlQueryExecutor {


    @Override
    public TableData execute(final DbInfo dbInfo, final Integer bound, PageInfo pageInfo, final String sql, final Object... args) throws Exception {
        if (args == null || args.length < 1 || StrUtil.isBlank(sql)) {
            return doExecute(dbInfo, bound, pageInfo, sql);
        }
        StringBuilder sb = new StringBuilder();
        boolean isBracket = false;
        int index = 0;
        for (char ch : sql.toCharArray()) {
            if (ch == '\'') {
                isBracket = !isBracket;
            }
            if (ch == '?' && !isBracket) {
                Object value = args[index++];
                if (value == null) {
                    sb.append("null");
                    continue;
                }
                if (value instanceof Number) {
                    sb.append(value);
                } else {
                    sb.append('\'').append(value).append('\'');
                }
                continue;
            }
            sb.append(ch);
        }
        return doExecute(dbInfo, bound, pageInfo, sb.toString());
    }

    public abstract TableData doExecute(final DbInfo dbInfo, final Integer bound, final PageInfo pageInfo, final String sql) throws Exception;


}
