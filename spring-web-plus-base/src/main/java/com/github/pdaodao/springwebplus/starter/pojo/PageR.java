package com.github.pdaodao.springwebplus.starter.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "接口返回分页数据封装")
public class PageR<T> extends R<List<T>> {
    private PageInfo pageInfo;

    public static <T> PageR<T> build(Long pageNum, Long pageSize, Long total, List<T> data) {
        final PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(total);
        final PageR pageR = new PageR();
        pageR.setData(data);
        pageR.setPageInfo(pageInfo);
        return pageR;
    }
}
