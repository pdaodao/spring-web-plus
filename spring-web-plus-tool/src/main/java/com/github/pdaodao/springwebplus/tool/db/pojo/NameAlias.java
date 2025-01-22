package com.github.pdaodao.springwebplus.tool.db.pojo;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.sql.TableAlias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameAlias {
    public String name;
    public String alias;

    public static NameAlias of(String name, String alias){
        if(StrUtil.isBlank(alias)){
            alias = null;
        }
        return new NameAlias(name.trim(), alias != null ? alias.trim() : alias);
    }

    public String alias(){
        if(StrUtil.isBlank(alias)){
            return name;
        }
        return alias;
    }

    /**
     * 给字段添加上表别名
     * @param tableAlias
     * @return
     */
    public String toString(String tableAlias){
        final StringBuilder sb = new StringBuilder();
        String f = name;
        if(StrUtil.isNotBlank(tableAlias)){
            // 给字段添加表别名
            f = TableAlias.addTableAlias(name, tableAlias);
        }
        sb.append(f);
        if(StrUtil.isNotBlank(alias) && !alias.equalsIgnoreCase(name)){
            sb.append(" AS ").append(alias);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        final NameAlias nameAlias = new NameAlias();
        nameAlias.setName("sum(f1)");
        nameAlias.setAlias("a");
        String t = nameAlias.toString("t1");
        System.out.println(t);
    }
}