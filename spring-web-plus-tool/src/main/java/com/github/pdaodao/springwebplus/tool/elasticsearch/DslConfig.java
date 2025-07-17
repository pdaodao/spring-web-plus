package com.github.pdaodao.springwebplus.tool.elasticsearch;

import lombok.Data;

// sql 转换为 Elasticsearch 查询时的配置项
@Data
public class DslConfig {
    // null 字段名称不变； true 转为小写； false 转为大写
    private Boolean fieldToLowCase = null;

    // 模糊搜索 当关键词长度大于该值时使用 minimumShouldMatch
    private Integer fuzzyThreshold = 0;
    // match query
    private Integer fuzzyMinMatch = 60;

    // 精确搜索时 当关键词长度大于该值时 启动最小匹配度
    private Integer accThreshold = 0;

    // phrase 跨度
    private Integer accPhraseSlop = 0;

    // match query 匹配时的 出现次数较少词
    private Float cutoffFrequency = 0.01f;
}