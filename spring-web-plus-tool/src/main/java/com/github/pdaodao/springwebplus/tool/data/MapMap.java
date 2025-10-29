package com.github.pdaodao.springwebplus.tool.data;

import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapMap<K1, K2, V>{
    private Map<K1, Map<K2, V>> map = new LinkedHashMap<>();

    public void put(final K1 k, Map<K2, V> mapItem){
        Preconditions.checkNotNull(k, "k is null.");
        map.put(k, mapItem);
    }

    public Map<K2, V> get(final K1 key){
        Preconditions.checkNotNull(key, "k is null.");
        return map.get(key);
    }

    public V get(final K1 key, final K2 itemKey){
        Preconditions.checkNotNull(itemKey, "itemKey is null.");
        final Map<K2, V> map = get(key);
        if(map == null){
            return null;
        }
        return map.get(itemKey);
    }

    public Set<Map.Entry<K1, Map<K2, V>>> entrySet(){
        if(map == null){
            return null;
        }
        return map.entrySet();
    }
}