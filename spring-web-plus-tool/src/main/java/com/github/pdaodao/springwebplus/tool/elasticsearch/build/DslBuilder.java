package com.github.pdaodao.springwebplus.tool.elasticsearch.build;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.elasticsearch.DslConfig;
import java.util.List;
import java.util.Objects;

/**
 * 转换器 ： EsOperator 到 Elasticsearch 查询语句
 */
public abstract class DslBuilder<T> {

    /**
     * 第2个以及后面的为字段名称
     *
     * @param args
     * @return
     */
    protected static String[] getFieldsFromArgs(List<Object> args, DslConfig dslConfig) {
        String[] fields = new String[args.size() - 1];
        for (int i = 1; i < args.size(); i++) {
            fields[i - 1] = args.get(i).toString();
        }
        return fields;
    }

    /**
     * 根据配置信息返回字段名称 大小写转换
     *
     * @param field
     * @param dslConfig
     * @return
     */
    public static String field(String field, DslConfig dslConfig) {
        if (dslConfig == null || dslConfig.getFieldToLowCase() == null) {
            return field;
        }
        if (dslConfig.getFieldToLowCase() == true)
            return field.toLowerCase();
        else
            return field.toUpperCase();
    }

    public static String getString(Object text) {
        if (text == null) return null;
        String str = Objects.toString(text);
        if (StrUtil.isEmpty(str)) return null;
        str = str.trim();
        return str;
    }

    /**
     * 对过滤值进行转义
     *
     * @param str
     * @return
     */
    protected abstract String escape(String str);

    /**
     * 该过滤符是否需要转义
     *
     * @return
     */
    protected boolean isNeedEscape() {
        return true;
    }

    /**
     * 转换为 elasticsearch 语法
     *
     * @param field     简单比较符时为 字段名称
     * @param dslConfig 配置
     * @param method    函数比较符  函数名称 简单比较时函数名称为空
     * @param args      参数  简单比较符长度为1
     * @return
     */
    public T build(String field, DslConfig dslConfig, String method, List<Object> args) throws Exception{
        if (StrUtil.isNotEmpty(field)){
            field = field(field, dslConfig);
        }
        if (CollectionUtil.isNotEmpty(args) && isNeedEscape()) {
            Object v = args.get(0);
            if (v != null && v instanceof String) {
                v = escape((String) v);
                args.set(0, v);
            }
        }
        if (CollectionUtil.isNotEmpty(args) && args.size() > 1) {
            for (int i = 1; i < args.size(); i++) {
                Object f = args.get(i);
                if (f == null) continue;
                args.set(i, field(Objects.toString(f), dslConfig));
            }
        }
        return doBuild(field, dslConfig, method, args);
    }

    public abstract T doBuild(String field, DslConfig dslConfig, String method, List<Object> args) throws Exception;

    /**
     * 参数的长度应该为 1 ， 否则抛出异常
     *
     * @param args
     */
    protected void checkOnlyOneArg(String operator, List<Object> args) {
        if (args == null || args.size() != 1)
            throw new IllegalArgumentException(operator + " should have only one arg. ");
    }

    /**
     * 参数的长度应该 大于等于2 否则抛出异常
     *
     * @param operator
     * @param args
     */
    protected void checkMoreThanOneArg(String operator, List<Object> args) {
        if (args == null || args.size() < 2)
            throw new IllegalArgumentException(operator + " should have more than one args. ");
    }

    /**
     * 对关键词进行转义 并且处理是否以关键词开头和结束
     *
     * @param text
     * @param fields
     * @return
     */
    protected KeyContext processKey(Object text, String... fields) {
        if (text == null) return null;
        String str = getString(text);
        if (str == null || fields == null || fields.length < 1) return null;
        boolean startWith = false;
        boolean endWith = false;

        if (str.startsWith("%")) endWith = true;
        if (str.endsWith("%")) startWith = true;

        if (startWith || endWith) {
            if (str.startsWith("%")) str = "*" + str.substring(1);
            if (str.endsWith("%")) str = str.substring(0, str.length() - 1) + "*";
        }
        return new KeyContext(str, startWith, endWith);
    }

    /**
     * 是否作为es查询的过滤项不计算score
     *
     * @return false 为不需要计算 score, true  为需要计算 score
     */
    public boolean isFilter() {
        return true;
    }

    public static class KeyContext {
        private final String key; // 转义后的关键词
        private final boolean startWith;  // 是否以该关键词开头
        private final boolean endWith;    // 是否以该关键词结束

        public KeyContext(String key, boolean startWith, boolean endWith) {
            this.key = key;
            this.startWith = startWith;
            this.endWith = endWith;
        }

        public String getKey() {
            return key;
        }

        public boolean isStartWith() {
            return startWith;
        }

        public boolean isEndWith() {
            return endWith;
        }

        public boolean isWith() {
            return startWith || endWith;
        }
    }
}