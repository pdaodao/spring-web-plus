package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import java.util.*;

@Data
public class BatchRowMap {
    private final List<String> pks;
    private final Map<String, TableRow> upsertMap = new LinkedHashMap<>(4096);
    private final Map<String, TableRow> deleteMap = new LinkedHashMap<>(4096);
    private int size = 0;

    public BatchRowMap(final List<String> pks) {
        this.pks = pks;
    }

    public synchronized BatchRowList toList(){
        final BatchRowList r = new BatchRowList(upsertMap.values(), deleteMap.values());
        upsertMap.clear();
        deleteMap.clear();
        size = 0;
        return r;
    }

    public int getSize() {
        return size;
    }

    public synchronized void add(final TableRow row){
        if(row == null){
            return;
        }
        size++;
        if(CollUtil.isEmpty(pks)){
            if(row.getKind() == null || RowKind.INSERT == row.getKind()
                    || RowKind.UPDATE_AFTER == row.getKind() || RowKind.UPDATE_AFTER == row.getKind()){
                upsertMap.put(IdUtil.simpleUUID(), row);
            }
            return;
        }
        final String pkValue = pkValue(row);
        if(row.getKind() == null || RowKind.INSERT == row.getKind()){
            deleteMap.remove(pkValue);
            upsertMap.put(pkValue, row);
            return;
        }
        if(RowKind.UPDATE_AFTER == row.getKind() || RowKind.UPDATE_AFTER == row.getKind() ){
            deleteMap.remove(pkValue);
            final TableRow old = upsertMap.get(pkValue);
            if(old == null){
                upsertMap.put(pkValue, row);
                return;
            }
            old.getData().mergeFrom(row.getData());
            return;
        }
        if(RowKind.DELETE == row.getKind()){
            upsertMap.remove(pkValue);
            deleteMap.put(pkValue, row);
        }
    }


    private String pkValue(final TableRow row){
        final StringBuilder sb = new StringBuilder();
        for(final String p: pks){
            sb.append(row.getData().getString(p, "-"));
            sb.append("-");
        }
        return sb.toString();
    }
}
