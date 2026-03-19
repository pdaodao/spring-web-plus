package com.github.pdaodao.springwebplus.tool.sql.util.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.scripting.xmltags.OgnlCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfBlock {
    private static final Pattern IF_PATTERN = Pattern.compile(
            "<if\\s+test\\s*=\\s*\"([^\"]*)\"\\s*>([\\s\\S]*?)</if>",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    // test 属性值，如 "a > 0"
    public final String testCondition;

    // 标签体内容，如 "AND name = #{name}"
    public final String sqlFragment;

    // 在原字符串中的起始位置（用于精确替换）
    public final int startIdx;

    // 结束位置
    public final int endIdx;

    public IfBlock(String testCondition, String sqlFragment, int startIdx, int endIdx) {
        this.testCondition = testCondition.trim();
        this.sqlFragment = sqlFragment.trim();
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }

    @Override
    public String toString() {
        return "IfBlock{test=\"" + testCondition + "\", sql=\"" + sqlFragment + "\"}";
    }

    /**
     * 提取所有 <if> 块（按出现顺序）
     */
    public static List<IfBlock> extractAllIfBlocks(String template) {
        if (template == null) return Collections.emptyList();
        final List<IfBlock> blocks = new ArrayList<>();
        final Matcher matcher = IF_PATTERN.matcher(template);
        while (matcher.find()) {
            String test = matcher.group(1);
            String content = matcher.group(2);
            blocks.add(new IfBlock(test, content, matcher.start(), matcher.end()));
        }
        return blocks;
    }

    public static String buildSql(final String template, Map<String, Object> params) {
        if(StrUtil.length(template) < 10){
            return template;
        }
        if(params == null){
            params = new HashMap<>();
        }
        // 按顺序提取所有 if 块
        final List<IfBlock> blocks = extractAllIfBlocks(template);
        if(CollUtil.isEmpty(blocks)){
            return template;
        }
        final StringBuilder result = new StringBuilder();
        int lastEnd = 0;

        for (IfBlock block : blocks) {
            // 添加上一个 if 结束 到 当前 if 开始 之间的静态 SQL
            result.append(template, lastEnd, block.startIdx);
            // 判断条件是否成立
            if (evaluate(block.testCondition, params)) {
                result.append(block.sqlFragment);
            }
            lastEnd = block.endIdx;
        }
        // 添加最后一个 if 之后的剩余部分
        result.append(template, lastEnd, template.length());
        return removeRedundantWhere(result.toString().trim());
    }

    private static boolean evaluate(String testCondition, final Map<String, Object> params){
        if(StrUtil.isBlank(testCondition)){
            return true;
        }
        testCondition = testCondition.trim();
        if(!testCondition.contains(" ")){
            Object v = params.get(testCondition);
            if(ObjectUtil.isEmpty(v)){
                return false;
            }
            if(StrUtil.equalsIgnoreCase(StrUtil.toString(v), "false")){
                return false;
            }
            if(StrUtil.equalsIgnoreCase(StrUtil.toString(v), "0")){
                return false;
            }
            return true;
        }
        final Object rr = OgnlCache.getValue(testCondition, params);
        if (ObjectUtil.equal(false, rr)) {
            return false;
        }
        return true;
    }


    /**
     * 移除 SQL 中多余的 WHERE 关键字。
     * 此方法处理 WHERE 后面紧跟各种 SQL 子句或结尾的情况。
     */
    public static String removeRedundantWhere(final String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return sql;
        }
        final String regex = "(?i)\\s+WHERE\\s+(?=\\s*(?:GROUP\\s+BY|ORDER\\s+BY|HAVING|LIMIT|JOIN|INNER\\s+JOIN|LEFT\\s+JOIN|RIGHT\\s+JOIN|FULL\\s+OUTER\\s+JOIN|CROSS\\s+JOIN|UNION|UNION\\s+ALL|INTERSECT|EXCEPT|FOR\\s+UPDATE|\\)|$))";
        // 将匹配到的 " WHERE " (包括前后空格) 替换为一个空格。
        return sql.replaceAll(regex, " ");
    }
}