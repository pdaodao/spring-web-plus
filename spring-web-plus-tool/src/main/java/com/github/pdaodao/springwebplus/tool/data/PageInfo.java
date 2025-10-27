package com.github.pdaodao.springwebplus.tool.data;

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

    public static PageInfo of(final Long pageNum, final Long pageSize){
        final PageInfo pp = new PageInfo();
        pp.setPageNum(pageNum);
        pp.setPageSize(pageSize);
        return pp;
    }

    public static PageInfo of(final Long pageNum, final Long pageSize, final Long total){
        final PageInfo pp = new PageInfo();
        pp.setPageNum(pageNum);
        pp.setPageSize(pageSize);
        pp.setTotal(total);
        return pp;
    }


    /**
     * 起始行数
     *
     * @return
     */
    public Long offset() {
        if (pageNum == null || pageNum < 1) {
            return 0l;
        }
        return (pageNum - 1) * pageSize;
    }

    public void check() {
        Preconditions.checkArgument(pageNum >= 1, "pageNum should >=1");
        Preconditions.checkArgument(pageSize >= 1, "pageSize should >=1");
    }
}