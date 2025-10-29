package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.util.*;

public class MapList<K, V> {
    private Map<K, List<V>> map = new LinkedHashMap<>();

    public void add(final K k, final V v){
        Preconditions.checkNotNull(k, "k is null.");
        if(!map.containsKey(k)){
            map.put(k, new ArrayList<>());
        }
        map.get(k).add(v);
    }

    public void addAll(final K k, final List<V> vv){
        if(CollUtil.isEmpty(vv)){
            return;
        }
        Preconditions.checkNotNull(k, "k is null.");
        if(!map.containsKey(k)){
            map.put(k, new ArrayList<>());
        }
        map.get(k).addAll(vv);
    }


    public List<V> get(final K k){
        return map.get(k);
    }

    public Set<Map.Entry<K, List<V>>> entrySet(){
        return map.entrySet();
    }
}