package com.github.pdaodao.springwebplus.tool.sql;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SqlFrame {
    private final String id;
    private final SQL sql;
    private transient List<SqlFrame> parentNode = new ArrayList<>();

    public SqlFrame(String id, SQL sql) {
        this.id = id;
        this.sql = sql;
    }

    public SqlFrame of(final String id, final SQL sql){
        final SqlFrame f = new SqlFrame(id, sql);
        return f;
    }

    public static SqlFrame of(final String id, final SQL sql, final List<SqlFrame> ps){
        final SqlFrame f = new SqlFrame(id, sql);
        f.setParentNode(ps);
        return f;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SqlFrame{");
        sb.append("sql=").append(sql);
        sb.append('}');
        return sb.toString();
    }

    public SqlFrame cloneToChild(final String id){
        final SQL clone = sql.clone();
         final SqlFrame f = new SqlFrame(id, clone);
         addParent(this);
         return f;
    }

    public void addParent(SqlFrame node) {
        addTo(node, parentNode);
    }

    private void addTo(SqlFrame node, List<SqlFrame> toList) {
        if (node == null) {
            return;
        }
        for (final SqlFrame ch : toList) {
            if (node.id.equals(ch.getId())) {
                return;
            }
        }
        toList.add(node);
    }
}
