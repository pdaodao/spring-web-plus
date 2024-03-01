package com.github.apengda.springwebplus.starter.pojo;

import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
@Schema(description = "分页查询请求参数")
public class PageRequestParam {
    @Schema(description = "页码 从1开始", example = "1")
    private Integer pageNum = 1;
    @Schema(description = "每页大小 默认为10", example = "10")
    private Integer pageSize = 10;
    @Schema(description = "排序字段,为实体类的属性名称")
    private String orderBy;
    @Schema(description = "是否为升序, 默认为true")
    private Boolean orderAsc = true;

    @JsonIgnoreProperties
    public static PageRequestParam from(final Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        final PageRequestParam pageRequestParam = new PageRequestParam();
        pageRequestParam.setPageNum(null);
        pageRequestParam.setPageSize(null);
        if (params.containsKey("pageNum") && params.containsKey("pageSize")) {
            Object pageNum = params.get("pageNum");
            if (pageNum != null) {
                pageRequestParam.setPageNum(NumberUtil.parseInt(Objects.toString(pageNum)));
                if (pageRequestParam.getPageNum() < 1) {
                    throw RestException.invalidParam("分页页号最小为1");
                }
            }
            Object pageSize = params.get("pageSize");
            if (pageSize != null) {
                pageRequestParam.setPageSize(NumberUtil.parseInt(Objects.toString(pageSize)));
                if (pageRequestParam.getPageSize() < 1) {
                    throw RestException.invalidParam("每页小于最小为1");
                }
            }
        }
        Object orderBy = params.get("orderBy");
        if (orderBy != null && orderBy instanceof String) {
            pageRequestParam.setOrderBy((String) orderBy);
        }
        Object orderAsc = params.get("orderAsc");
        if (orderAsc != null) {
            String orderAscStr = Objects.toString(orderAsc);
            if (orderAscStr.equalsIgnoreCase("desc")) {
                pageRequestParam.setOrderAsc(false);
            } else if (orderAscStr.equalsIgnoreCase("asc")) {
                pageRequestParam.setOrderAsc(true);
            } else {
                pageRequestParam.setOrderAsc(Boolean.parseBoolean(orderAscStr));
            }
        }
        return pageRequestParam;
    }

    @JsonIgnoreProperties
    public static String keys() {
        return "pageNum,pageSize,orderBy,orderAsc,";
    }


    @Schema(description = "当不用Mybatis提供的分页时使用，需与Mybatis计算的offset逻辑保持一致", hidden = true)
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }

    public Page toPage() {
        final Page page = new Page();
        page.setCurrent(getPageNum());
        page.setSize(getPageSize());
        if (StringUtils.isNotBlank(getOrderBy())) {
            page.addOrder( getOrderAsc() == true ? OrderItem.asc(getOrderBy()):OrderItem.desc(getOrderBy()));
        }
        // 关闭count 优化
        page.setOptimizeCountSql(false);
        return page;
    }
}