package com.github.pdaodao.springwebplus.tool.db.util;

import com.github.pdaodao.springwebplus.tool.db.core.FilterTree;
import com.github.pdaodao.springwebplus.tool.db.pojo.JoinType;
import com.github.pdaodao.springwebplus.tool.db.sql.SQL;
import com.github.pdaodao.springwebplus.tool.db.sql.TableAliasCounter;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class SqlTest {
    public static boolean sqlEqual(String sql, String right) {
        sql = replaceBlank(sql);
        right = replaceBlank(right);
        return sql.equals(right);
    }

    private static String replaceBlank(String t) {
        return t.toLowerCase().replaceAll("\\s+", " ")
                .replaceAll("\\(\\s+", "(")
                .replaceAll("\\s+\\)", ")")
                .replaceAll("\\s*\\?\\s*", "?")
                .trim();
    }


    @Test
    public void simpleOneTable(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1, f2, f3")
                .from("t1").where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "f7 desc");
        final String sql = t1.toString();
        final String right = "select f1, f2, f3 from t1  where ( f4 = 1 or f5 like '%北京%' ) order by f6 asc, f7 desc";
        Assert.assertTrue("WhereUtils tt0",  sqlEqual(sql, right));
    }


    @Test
    public void simpleInsert(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
         t1.insertInto("t1").values("f1", ":f1");

         System.out.println(t1.toString());
    }

    @Test
    public void testFilterTree(){
        final FilterTree f = new FilterTree();
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1", "f2")
                .from("t1").where("a = 1 ")
                .or()
                .where("b = 2")
                .where("c = 3");
        System.out.println(t1.toString());
    }

    @Test
    public void simpleProject(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1, f2, f3")
                .from("t1").where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "f7 desc");

        final SQL t2 = t1.project()
                .select("f1, f2")
                .orderBy("f1 asc");

        final String sql = t1.toString();
        final String right = "select f1, f2, f3 from t1  where ( f4 = 1 or f5 like '%北京%' ) order by f6 asc, f7 desc";
        Assert.assertTrue("WhereUtils tt0",  sqlEqual(sql, right));
        final String mSql = t2.toString();
        System.out.println(mSql);
    }

    @Test
    public void filterWithGroup(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1, f2, f3, sum(f4)")
                .from("t1").where(" a = 1")
                .groupBy("f3")
                .orderBy("f6 asc", "f7 desc");
        final String sql = t1.toString();
        System.out.println(sql);
        final String right = "select f1, f2, f3, sum(f4) from t1  where ( a = 1) group by f3 order by f6 asc, f7 desc";
        Assert.assertTrue("filterWithGroup",  sqlEqual(sql, right));
    }

    @Test
    public void simpleJoin1() {
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1, f2, f3")
                .from("t1").
                where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "f7 desc");

        final SQL t2 = SQL.of(holder);
        t2.select("ff1,ff2")
                .from("t2")
                .where("fn(ff1) = 1");

        SQL joined = t1.join(JoinType.INNER, t2, "{}.f1 = {r}.ff1");

        System.out.println(joined.toString());
    }


    @Test
    public void simpleUnion1() {
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder);
        t1.select("f1, f2, f3")
                .from("t1").
                where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "f7 desc");

        final SQL t2 = SQL.of(holder);
        t2.select("ff1,ff2, ff3")
                .from("t2");

        SQL union = t1.union(t2);
        union.select("f1")
                .where("f1 = 3");
        final String sql = union.toString();
        final String right = "SELECT f1\n" +
                " (\n" +
                " SELECT f1, f2, f3\n" +
                "    FROM t1\n" +
                "    WHERE (f4 = 1 or f5 like '%北京%')\n" +
                "    ORDER BY f6 asc, f7 desc\n" +
                " UNION ALL \n" +
                "    SELECT ff1, ff2, ff3\n" +
                "    FROM t2\n" +
                ") A\n" +
                "WHERE (f1 = 3)";

        System.out.println(sql);
        Assert.assertTrue("simpleUnion1",  sqlEqual(sql, right));
    }



    @Test
    public void threeTableJoin(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder)
                .select("f1, f2, f3")
                .from("tf1")
                .where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "{}.f7 desc");

        final SQL t2 = SQL.of(holder)
                .select("t1,t2")
                .from("tf2");

        final SQL t3 = SQL.of(holder)
                .select("s1,sum(s2) as ss")
                .from("tf3")
                .orderBy("s1 asc")
                .groupBy("s1");

        final SQL join1 = t1.join(JoinType.LEFT, t2, "{tf1}.f1 = {tf2}.t1");

        final SQL join2 = join1.join(JoinType.INNER, t3, "{tf2}.t1 = {r}.s1");

        System.out.println("============");
        final String right = "SELECT A.f1, A.f2, A.f3, \n" +
                "    B.t1, B.t2, \n" +
                "    C.ss\n" +
                "FROM tf1 A\n" +
                "LEFT JOIN tf2 B ON A.f1 = B.t1\n" +
                "INNER JOIN  ( SELECT s1,sum(s2) AS ss FROM tf3 GROUP BY s1 ORDER BY s1 asc ) C ON B.t1 = C.s1\n" +
                "WHERE (A.f4 = 1 or A.f5 like '%北京%')";
        System.out.println(join2.toString());
        Assert.assertTrue("threeTableJoin",  sqlEqual(join2.toString(), right));
    }

    @Test
    public void testJoinUnion(){
        final TableAliasCounter holder = new TableAliasCounter();
        final SQL t1 = SQL.of(holder)
                .select("f1, f2")
                .from("tf1")
                .where("f4 = 1 or f5 like '%北京%'")
                .orderBy("f6 asc", "{}.f7 desc");

        final SQL t2 = SQL.of(holder)
                .select("t1,t2")
                .from("tf2");

        final SQL t3 = SQL.of(holder)
                .select("s1,sum(s2) as ss")
                .from("tf3")
                .orderBy("s1 asc")
                .groupBy("s1");

        final SQL join1 = t1.join(JoinType.LEFT, t2, "{tf1}.f1 = {tf2}.t1");
        join1.select("f1, t1");

        final SQL union = join1.union(t3);
        final String sql = union.toString();
        final String right = "SELECT f1, t1\n" +
                "FROM (\n" +
                "    SELECT A.f1, A.f2, B.t1, B.t2\n" +
                "    FROM tf1 A\n" +
                "    LEFT JOIN tf2 B ON A.f1 = B.t1\n" +
                "    WHERE (A.f4 = 1 or A.f5 like '%北京%')\n" +
                ") C\n" +
                " UNION ALL \n" +
                "SELECT s1,sum(s2) AS ss\n" +
                "FROM tf3\n" +
                "GROUP BY s1\n" +
                "ORDER BY s1 asc";

        Assert.assertTrue("testJoinUnion", sqlEqual(sql, right));
    }
}
