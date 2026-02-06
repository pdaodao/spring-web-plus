package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.sql.util.SqlUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 语法树中的查询字段
 */
@Data
public class SelectFromField {
    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段别名
     */
    private String alias;

    /**
     * 表别名
     */
    private String tableAlias;

    /**
     * 物理表名称
     */
    private String tableName;

    /**
     * 来源字段
     */
    private List<SelectFromField> froms;


    // 是否已经处理过了
    private boolean isRootProcessed = false;

    /**
     * 是否为字面量
     */
    private boolean isLiteral = false;

    /**
     * 函数名称
     */
    private String fn;

    public String getAlias() {
        if(alias == null){
            return name;
        }
        return alias;
    }

    public String getName() {
        if(name == null){
            return alias;
        }
        return name;
    }

    public static SelectFromField of(String name, final String alias){
        if(StrUtil.isNotBlank(name) && !name.contains("(") && name.contains(".")){
            name = name.substring(name.indexOf(".") + 1);
        }
        final SelectFromField f = new SelectFromField();
        f.setName(name);
        f.setAlias(alias == null ? name : alias);
        return f;
    }

    /**
     * 添加来源字段
     * @param f
     */
    public void addFrom(final SelectFromField f){
        if(f == null){
            return;
        }
        if(froms == null){
            froms = new ArrayList<>();
        }
        froms.add(f);
    }

    /**
     * 处理字段的中文名称
     * @param fieldMap
     * @return
     */
    public String processFieldTitle(final LinkedCaseInsensitiveMap<String> fieldMap, final LinkedCaseInsensitiveMap<String> retMap) {
        if(ObjectUtil.equal(true, isLiteral)){
            final String title = "常量"+name;
            retMap.put(getAlias(), title);
            return title;
        }
        final String titleSufix = titleSuffix();
        // 有来源
        if(CollUtil.isEmpty(froms) && StrUtil.isNotBlank(tableName)){
            // select *
            if(StrUtil.equals("*", name)){
                for(Map.Entry<String, String> entry: fieldMap.entrySet()){
                    final String key = entry.getKey();
                    if(key.startsWith(tableName+".")){
                        retMap.put(key.substring(key.indexOf(".") + 1), entry.getValue() + titleSufix);
                    }
                }
            }
            final String title = fieldMap.get(tableName + "."+name);
            if(StrUtil.isNotBlank(title)){
                retMap.put(getAlias(), title + titleSufix);
                return title + titleSufix;
            }
        }
        if(CollUtil.isNotEmpty(froms)){
            final StringBuilder sb = new StringBuilder();
            for(final SelectFromField ff: froms){
                if(ff.isLiteral){
                    continue;
                }
                final String subTitle = ff.processFieldTitle(fieldMap, retMap);
                sb.append(subTitle);
            }
            if(sb.length() > 3){
                sb.append(titleSufix);
                retMap.put(getAlias(), sb.toString());
            }
            return sb.toString();
        }
        return StrUtil.EMPTY;
    }

    public void setAlias(String alias) {
        this.alias = SqlUtil.dropSqlEscape(alias);
    }

    private String titleSuffix(){
        if(StrUtil.isBlank(fn)){
            return StrUtil.EMPTY;
        }
        if(StrUtil.equals("sum", fn)){
            return "总和";
        }
        if(StrUtil.equals("count", fn)){
            return "总数";
        }
        if(StrUtil.equals("avg", fn)){
            return "均值";
        }
        if(StrUtil.equals("min", fn)){
            return "最小值";
        }
        if(StrUtil.equals("max", fn)){
            return "最大值";
        }
        if(StrUtil.equals("Division", fn)){
            return "比率";
        }
        return StrUtil.EMPTY;
    }
}
