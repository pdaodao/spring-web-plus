package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.ListUtil;
import java.util.Collection;

public class BatchRowList extends BatchList<TableRow>{
    public BatchRowList(){
    }

    public BatchRowList(Collection<TableRow> upserts, Collection<TableRow> deletes) {
        super(ListUtil.toList(upserts), ListUtil.toList(deletes));
    }
}
