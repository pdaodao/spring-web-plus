package com.github.pdaodao.springwebplus.tool.flow.processor.groupagg;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组聚合
 */
@Data
public class GroupAggInfo {
    // 分组字段
    private List<GroupTableColumn> keys = new ArrayList<>();
    // 聚合字段
    private List<GroupTableColumn> aggs;
    
    @Data
    public static class GroupTableColumn {
        // 字段名称
        private String name;
        // 字段别名
        private String alias;
        // 函数
        private String fn;
    }
}