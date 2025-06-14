package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;

public class ListDataset implements Dataset{
    private List<TableRow> list;
    private int index;

    public ListDataset() {
    }

    public ListDataset(List<TableRow> list) {
        this.list = list;
    }

    public static ListDataset of(final TableRow... rows){
        final List<TableRow> list = new ArrayList<>();
        if(rows != null){
            for(final TableRow r: rows){
                list.add(r);
            }
        }
        return new ListDataset(list);
    }

    public static ListDataset of(final TableRowData... rows){
        final List<TableRow> list = new ArrayList<>();
        if(rows != null){
            for(final TableRowData d: rows){
                list.add(new TableRow(null, d));
            }
        }
        return new ListDataset(list);
    }

    public synchronized ListDataset add(final TableRow row){
        if(row == null){
            return this;
        }
        if(list == null){
            list = new ArrayList<>();
        }
        list.add(row);
        return this;
    }

    public synchronized ListDataset add(final TableRowData row){
        if(row == null){
            return this;
        }
        return add(new TableRow(null, row));
    }

    @Override
    public Long total() {
        return CollUtil.size(list) + 0L;
    }

    @Override
    public TableRow next() throws Exception {
        if(index < CollUtil.size(list)){
            return list.get(index++);
        }
        return null;
    }
}
