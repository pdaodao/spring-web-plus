package com.github.pdaodao.springwebplus.base.pojo;

import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.Data;

/**
 * 分页信息
 */
@Data
public class PageInfo {
    /**
     * 页码 从1开始
     */
    private Long pageNum;

    /**
     * 每页大小 >=1
     */
    private Long pageSize;

    /**
     * 总数据行数
     */
    private Long total;

    public void check() {
        Preconditions.checkArgument(pageNum >= 1, "pageNum should >=1");
        Preconditions.checkArgument(pageSize >= 1, "pageSize should >=1");
    }
}