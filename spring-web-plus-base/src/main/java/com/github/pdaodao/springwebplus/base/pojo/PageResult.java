package com.github.pdaodao.springwebplus.base.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "接口返回分页数据封装")
public class PageResult<T> extends RestResponse<List<T>> {
    private PageInfo pageInfo;

    public static <T> PageResult<T> build(Long pageNum, Long pageSize, Long total, List<T> data) {
        final PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        final PageResult pageR = new PageResult();
        pageR.setData(data);
        pageR.setPageInfo(pageInfo);
        return pageR;
    }
}
