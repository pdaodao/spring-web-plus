package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.ListUtil;

import java.util.Collection;
import java.util.List;

public class CdcBatchListData {
    private final List<StreamRow> upserts;
    private final List<StreamRow> deletes;

    public CdcBatchListData(Collection<StreamRow> upserts, Collection<StreamRow> deletes) {
        this.upserts = ListUtil.toList(upserts);
        this.deletes = ListUtil.toList(deletes);
    }

    public List<StreamRow> getUpserts() {
        return upserts;
    }

    public List<StreamRow> getDeletes() {
        return deletes;
    }
}
