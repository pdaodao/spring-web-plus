package com.github.pdaodao.springwebplus.tool.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 数据
     */
    private Collection<T> list;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;

    public static <T> PageResult<T> build(Long pageNum, Long pageSize, Long total, List<T> data) {
        final PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        final PageResult pageR = new PageResult();
        pageR.setList(data);
        pageR.setPageInfo(pageInfo);
        return pageR;
    }

    public static <T> PageResult<T> of(List<T> data) {
        final PageResult pageR = new PageResult();
        pageR.setList(data);
        return pageR;
    }

    public PageResult<T> add(T row) {
        if (row == null) {
            return this;
        }
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(row);
        return this;
    }
}
