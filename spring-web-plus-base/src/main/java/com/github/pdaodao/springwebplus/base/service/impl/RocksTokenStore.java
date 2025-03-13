package com.github.pdaodao.springwebplus.base.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import com.github.pdaodao.springwebplus.base.service.TokenStore;
import com.github.pdaodao.springwebplus.base.util.RocksDbUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// https://github.com/nitrite/nitrite-java
public class RocksTokenStore implements TokenStore {
    private final RocksDB rocksDB;

    public RocksTokenStore() throws Exception{
        rocksDB = RocksDbUtil.open(".token");
    }

    @Override
    public List<TokenInfo> byPrefix(final String prefix) throws Exception{
        final List<TokenInfo> list = new ArrayList<>();
        try (RocksIterator iterator = rocksDB.newIterator()) {
            iterator.seek(prefix.getBytes());
            while (iterator.isValid()) {
                final String key = new String(iterator.key());
                if (!key.startsWith(prefix)) {
                    break;
                }
                final String value = new String(iterator.value());
                list.add(JsonUtil.objectMapper.readValue(value, TokenInfo.class));
                iterator.next();
            }
        }
        return list;
    }

    @Override
    public TokenInfo byToken(String token) throws Exception{
        Preconditions.checkNotBlank(token, "token is empty");
        final byte[] bytes = rocksDB.get(StrUtil.utf8Bytes(token));
        if(bytes == null){
            return null;
        }
        final String ct = new String(bytes);
        final TokenInfo tokenInfo = JsonUtil.objectMapper.readValue(ct, TokenInfo.class);
        storeToken(token, tokenInfo);
        return tokenInfo;
    }

    @Override
    public void removeToken(String token) throws Exception{
        Preconditions.checkNotBlank(token, "token is empty");
        rocksDB.delete(StrUtil.utf8Bytes(token));
    }

    @Override
    public void storeToken(String token, TokenInfo tokenInfo) throws Exception{
        Preconditions.checkNotBlank(token, "token is empty");
        Preconditions.checkNotNull(tokenInfo, "tokenInfo is null.");
        tokenInfo.setToken(token);
        tokenInfo.setLastAccessTime(new Date());
        rocksDB.put(StrUtil.utf8Bytes(token), StrUtil.utf8Bytes(JsonUtil.toJsonString(tokenInfo)));
    }

    public static void main(String[] args) throws Exception{
        RocksTokenStore store = new RocksTokenStore();
        final TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setUserId("2");
        tokenInfo.setUsername("admin");
//        store.storeToken("u1-123", tokenInfo);
//        ThreadUtil.sleep(100);
        final TokenInfo ret = store.byToken("u1-123");

        System.out.println("ret>"+ret);
    }

    public static void main2(String[] args) {
        final String p = "/Users/peng/Workspace";

        final List<File> files = FileUtil.loopFiles(FileUtil.newFile(p), 20, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".java");
            }
        });
        for(final File f: files){
            final String ct = FileUtil.readUtf8String(f);
            if(ct.contains("ColumnFamilyDescriptor")){
                System.out.println(f.getAbsolutePath());
            }
        }
    }
}
