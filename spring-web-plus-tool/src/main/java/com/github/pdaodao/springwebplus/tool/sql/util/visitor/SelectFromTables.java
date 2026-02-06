package com.github.pdaodao.springwebplus.tool.sql.util.visitor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SelectFromTables {
    private LinkedCaseInsensitiveMap<SelectFromTable> map;
    private SelectFromTable root;

    public static SelectFromTables of(){
        return new SelectFromTables();
    }

    /**
     * 找到字段的最终来源
     */
    public void findRoot(){
        if(root == null || CollUtil.isEmpty(root.getFields())){
            return;
        }
        for(final SelectFromField f: root.getFields()){
            findRootFrom(f, 1);
        }
    }

    private void findRootFrom(final SelectFromField field, final Integer step){
        if(field == null){
            return;
        }
        if(step > 50){
            field.setRootProcessed(true);
            return;
        }
        if(CollUtil.isNotEmpty(field.getFroms())){
            final List<SelectFromField> rr = new ArrayList<>();
            for(final SelectFromField sub: field.getFroms()){
                findRootFrom(sub,  step + 1);
                if(CollUtil.isEmpty(sub.getFroms())){
                    rr.add(sub);
                }else{
                    if(StrUtil.isNotBlank(sub.getFn())){
                        for(final SelectFromField subF: sub.getFroms()){
                            if(StrUtil.isEmptyIfStr(subF.getFn())){
                                subF.setFn(sub.getFn());
                            }
                        }
                    }
                    rr.addAll(sub.getFroms());
                }
            }
            field.setRootProcessed(true);
            field.setFroms(rr);
        }
        if(ObjectUtil.equal(true, field.isLiteral())){
            field.setRootProcessed(true);
            return;
        }
        field.setRootProcessed(true);
        final SelectFromTable table = map.get(field.getTableAlias());
        if(table == null){
            return;
        }
        field.setTableName(table.getName());
    }

    public SelectFromTable addTable(final String tableName, String alias){
        if(map == null){
            map = new LinkedCaseInsensitiveMap<>();
        }
        if(StrUtil.isBlank(alias)){
            alias = tableName;
        }
        if(map.containsKey(alias)){
            return map.get(alias);
        }
        final SelectFromTable tt = SelectFromTable.of(this, tableName, alias);
        if(root == null){
            root = tt;
        }
        map.put(alias, tt);
        return tt;
    }

    public String getTableNameBy(final String nameOrAlias) {
        if(StrUtil.isBlank(nameOrAlias) || !map.containsKey(nameOrAlias.trim())){
            return null;
        }
       return  map.get(nameOrAlias.trim()).getName();
    }

    public SelectFromTable getBy(final String tableNameOrAlias){
        if(StrUtil.isBlank(tableNameOrAlias) || !map.containsKey(tableNameOrAlias.trim())){
            return null;
        }
        return map.get(tableNameOrAlias.trim());
    }

    public List<String> tableList(){
        final List<String> ret = new ArrayList<>();
        for(SelectFromTable tt: map.values()){
            if(tt.isTable()){
                ret.add(tt.getName());
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "SelectFromTables{" +
                "map=" + map +
                ", root=" + root +
                '}';
    }
}
