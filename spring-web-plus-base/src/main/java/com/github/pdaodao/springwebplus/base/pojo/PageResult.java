package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Collection;
import java.util.List;

@Data
@Schema(description = "接口返回分页数据封装")
public class PageResult<T> {
    @Schema(description = "数据")
    private Collection<T> list;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "分页数据")
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
}
