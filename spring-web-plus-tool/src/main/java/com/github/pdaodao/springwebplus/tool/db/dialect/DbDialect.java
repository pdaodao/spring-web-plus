package com.github.pdaodao.springwebplus.tool.db.dialect;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.core.DbInfo;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据库方言
 */
public interface DbDialect {
    Map<String, Set<String>> keywordsMap = new ConcurrentHashMap<>();

    DbType dbType();

    DbMetaLoader metaLoader(final DbInfo dbInfo);

    /**
     * 是否支持事物
     *
     * @return
     */
    default boolean isSupportTransaction() {
        return true;
    }


    /**
     * 驱动名称
     *
     * @return
     */
    String driverName();

    String buildUrl(final DbInfo dbInfo);

    /**
     * 数据库是否支持schema
     *
     * @return
     */
    boolean isSupportSchema();

    /**
     * jdbc读取数据时的拉取大小
     *
     * @return
     */
    default Integer fetchSize() {
        return null;
    }

    /**
     * 转义符
     *
     * @return
     */
    String escape();


    /**
     * 分页语句
     *
     * @param sql    sql语句
     * @param offset 偏移行数
     * @param size   返回数据行数
     * @return
     */
    String pageSql(final String sql, Integer offset, Integer size);


    /**
     * 关键字文件
     *
     * @return
     */
    default String keywordsFile() {
        return null;
    }


    /**
     * 数据库关键字
     *
     * @return
     */
    default Set<String> keywords() {
        final String name = this.getClass().getName();
        Set<String> sets = keywordsMap.get(name);
        if (sets != null) {
            return sets;
        }
        sets = new HashSet<>();
        final String kf = keywordsFile();
        if (StrUtil.isNotBlank(kf)) {
            try {
                return FileUtil.readUtf8Lines(ResourceUtil.getResource(kf, this.getClass()))
                        .stream()
                        .filter(t -> !StrUtil.isBlank(t))
                        .filter(t -> !t.startsWith("#"))
                        .map(t -> t.trim().toLowerCase())
                        .collect(Collectors.toSet());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        keywordsMap.put(name, sets);
        return sets;
    }

    default boolean isKeyword(String name) {
        if (StrUtil.isBlank(name)) {
            return false;
        }
        final Set<String> ks = keywords();
        if (CollectionUtil.isEmpty(ks)) {
            return true;
        }
        if (name.contains("-")) {
            return true;
        }
        if (name.trim().toLowerCase().contains("value") || name.contains(".")) {
            return true;
        }
        return ks.contains(name.trim().toLowerCase());
    }

    /**
     * 使用转义符号
     *
     * @param identifier
     * @return
     */
    default String quoteIdentifier(String identifier) {
        if (StrUtil.isBlank(escape()) || StrUtil.isBlank(identifier)) {
            return identifier;
        }
        identifier = identifier.trim();
        if (isKeyword(identifier)) {
            return escape() + identifier + escape();
        }
        return identifier;
    }

    /**
     * 类型转换器
     *
     * @return
     */
    DataTypeConverter dataTypeConverter();

    /**
     * ddl 生成器
     *
     * @return
     */
    DbDDLGen ddlGen();
}
