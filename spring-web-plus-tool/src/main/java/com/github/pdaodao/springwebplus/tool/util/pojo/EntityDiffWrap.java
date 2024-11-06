package com.github.pdaodao.springwebplus.tool.util.pojo;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityDiffWrap<T> {
    private List<T> toInserts;

    private List<T> toUpdates;

    private List<T> toDeletes;

    private LinkedCaseInsensitiveMap<T> oldMap;

    public boolean isDiff() {
        return CollUtil.isNotEmpty(toInserts) || CollUtil.isNotEmpty(toDeletes);
    }

    public void addInsert(T t) {
        if (t == null) {
            return;
        }
        if (toInserts == null) {
            toInserts = new ArrayList<>();
        }
        toInserts.add(t);
    }

    public void addUpdate(T t) {
        if (t == null) {
            return;
        }
        if (toUpdates == null) {
            toUpdates = new ArrayList<>();
        }
        toUpdates.add(t);
    }

    public void addDelete(T t) {
        if (t == null) {
            return;
        }
        if (toDeletes == null) {
            toDeletes = new ArrayList<>();
        }
        toDeletes.add(t);
    }
}
