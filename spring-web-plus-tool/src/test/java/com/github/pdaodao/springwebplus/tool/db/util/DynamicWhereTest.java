package com.github.pdaodao.springwebplus.tool.db.util;

import com.github.pdaodao.springwebplus.tool.db.core.FilterItem;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import com.github.pdaodao.springwebplus.tool.db.core.WhereOperator;

import java.util.ArrayList;
import java.util.List;

public class DynamicWhereTest {
    public static void main1(String[] args) {
        String sql = "a = #{ a} and b = #{b} and c > #c and d < #d  and e >= :e order by a desc";
        // 替换 #{ param } 为 #param
        sql = sql.replaceAll("#\\{\\s*([^}]+?)\\s*}", "#$1");

        // 替换 ${ param } 为 $param
        sql = sql.replaceAll("\\$\\{\\s*([^}]+?)\\s*}", "\\$$1");

        System.out.println(sql);
    }

    public static void main(String[] args) throws Exception {
        final String sql = "select * from t1 where a = #{a} and ${aa} = #{ab} if{b > 1, and b = #b and c > #{c} and d < #d }  and f1(#{e}) > 3 and f is not null order by a desc";
        final List<FilterItem> fs = new ArrayList<>();
        fs.add(FilterItem.of("a", WhereOperator.eq, "a"));
        fs.add(FilterItem.of("b", WhereOperator.eq, "2"));

        final SqlWithMapParams ret = SqlUtil.dynamicFilter(sql, fs);
        System.out.println(ret.getSql());
        System.out.println(ret.toNamedSql().getSql());
    }
}
