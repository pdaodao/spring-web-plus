package com.github.pdaodao.springwebplus.tool.db.util;

import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.tool.db.core.SqlWithMapParams;
import com.github.pdaodao.springwebplus.tool.db.dialect.mysql.MysqlDialect;
import com.github.pdaodao.springwebplus.tool.sql.Where;
import org.junit.Test;

public class WhereTest {

    @Test
    public void t1(){
        final Where w = Where.of();
        w.eq("f1", "abc");
        w.eq("f2", 2);
        w.lt("f3", 3);
        w.in("f4", ListUtil.list(false, 1, 2, 3));
        final SqlWithMapParams p = new SqlWithMapParams();
        System.out.println(w.toSql(new MysqlDialect(), p));
        System.out.println("hello");
    }
}
