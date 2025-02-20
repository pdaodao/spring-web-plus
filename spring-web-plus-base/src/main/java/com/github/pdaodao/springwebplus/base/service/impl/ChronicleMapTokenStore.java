package com.github.pdaodao.springwebplus.base.service.impl;

import cn.hutool.core.io.FileUtil;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import java.io.IOException;

public class ChronicleMapTokenStore implements TokenStore {
    private ChronicleMap<String, TokenInfo> map;

    public ChronicleMapTokenStore() throws IOException {
        map = ChronicleMapBuilder.of(String.class, TokenInfo.class)
                .entries(1000) // 设置最大条目数（可选）
                .averageKeySize(50) // 设置键的平均大小（可选）
                .averageValueSize(200) // 设置值的平均大小（可选）
                .createPersistedTo(FileUtil.file(".token")); // 存储到指定的文件路径
    }

    @Override
    public TokenInfo byToken(String token) throws Exception {
        return map.get(token);
    }

    @Override
    public void removeToken(String token) throws Exception {
        map.remove(token);
    }

    @Override
    public void storeToken(String token, TokenInfo tokenInfo) throws Exception {
        map.put(token, tokenInfo);
    }

    public static void main(String[] args) throws Exception{
        ChronicleMapTokenStore store = new ChronicleMapTokenStore();
        store.storeToken("aa", new TokenInfo());
        System.out.println("hello");
    }
}
