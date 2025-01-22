package com.github.pdaodao.springwebplus.tool.db.sql;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.sql.frame.SqlTokenizer;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class TableAlias {
    public static final Pattern Field_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]+$");

    private String tableAlias;
    private TableAliasCounter counter;

    public TableAlias(TableAliasCounter counter) {
        this.counter = counter;
    }

    public static TableAlias of(final TableAliasCounter counter){
        return new TableAlias(counter);
    }

    @Override
    public String toString() {
        return tableAlias;
    }

    public String alias(){
        if(StrUtil.isNotBlank(tableAlias)){
            return tableAlias;
        }
        tableAlias = counter.getTableAlias();
        return tableAlias;
    }

    public static final Set<String> KeyWords = new HashSet<>();

    static {
        KeyWords.addAll(SqlTokenizer.SEPARATORS);
        KeyWords.add("LIKE");
        KeyWords.add("BETWEEN");
        KeyWords.add("RLIKE");
        KeyWords.add("LLIKE");
    }

    /**
     * 给 sql语句中的字段添加表别名
     * @param sql
     * @param tableAlias
     * @return
     */
    public static String addTableAlias(final String sql, final String tableAlias){
        if(StrUtil.isBlank(tableAlias)){
            return sql;
        }
        final SqlTokenizer tokenizer = new SqlTokenizer(sql);
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for(final SqlTokenizer.SqlToken token: tokenizer.getTokens()){
            String v = token.getValue();
            if(!KeyWords.contains(v.toUpperCase())){
                if(Field_PATTERN.matcher(v).find() && !StrUtil.equals("(", tokenizer.nextSqlToken(index++))){
                    v = tableAlias+"."+v;
                }
            }
            sb.append(v);
        }
        return sb.toString();
    }
}
