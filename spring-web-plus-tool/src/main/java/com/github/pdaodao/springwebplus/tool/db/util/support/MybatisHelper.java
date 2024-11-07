package com.github.pdaodao.springwebplus.tool.db.util.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.tool.db.core.FilterItem;
import org.apache.ibatis.scripting.xmltags.OgnlCache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * 小于号 <：应该用 &lt; 或 <![CDATA[<]]> 转义。
 * 大于号 >：应该用 &gt; 或 <![CDATA[>]]> 转义。
 * 引号 '：应该用 \' 或 <![CDATA[']]><![CDATA[>]]> 转义。
 * 双引号 "：应该用 \" 或 <![CDATA["]]><![CDATA[>]]> 转义。
 * 特殊符号 $：应该用 &#36; 转义。
 * 特殊符号 {}：应该使用占位符 #{} 来代替。
 * 特殊符号 /：在 XML 中需要用 <![CDATA[/]]> 转义，否则会被解析为闭合标签。
 */
public class MybatisHelper {
    private static final Pattern ESCAPE_LT_PATTERN = Pattern.compile("<([\\d'\"\\s=>#$?(])");
    private static final String ESCAPE_LT_REPLACEMENT = "&lt;$1";

    private static final GenericTokenParser IF_TOKEN_PARSER = new GenericTokenParser("if{", "}", true);

    private static final GenericTokenParser IF_PARAM_TOKEN_PARSER = new GenericTokenParser("if{", ",", true);


    /**
     * 处理?{}参数
     *
     * @param sql
     * @return
     */
    public static boolean hasIf(final String sql) {
        return sql.contains("if{");
    }

    public static String processIf(final String sql, final Map<String, Object> map) {
        // 处理if{}条件
        return IF_TOKEN_PARSER.parse(sql.trim(), text -> {
            AtomicBoolean ifTrue = new AtomicBoolean(false);
            String val = IF_PARAM_TOKEN_PARSER.parse("if{" + text, param -> {
                // todo 未来替换为自研引擎
                final Object rr = OgnlCache.getValue(param, map);
                if (ObjectUtil.equal(true, rr)) {
                    ifTrue.set(true);
                }
                return null;
            });
            return ifTrue.get() ? val : "";
        });
    }

    public static String processIf(final String sql, final List<FilterItem> filterItems) {
        if (CollUtil.isEmpty(filterItems)) {
            return sql;
        }
        return processIf(sql, FilterItem.toParamMap(filterItems));
    }


    public static boolean has(final String sql) {
        return sql.contains("</");
    }

    private static String escapeXml(String xml) {
        return ESCAPE_LT_PATTERN.matcher(xml).replaceAll(ESCAPE_LT_REPLACEMENT);
    }

}
