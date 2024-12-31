package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import lombok.Data;

import java.util.*;

@Data
public class PageResult<T> {
    /**
     * 数据
     */
    private Collection<T> list;

    /**
     * 分页
     */
    private PageInfo pageInfo;

    /**
     * 字段列表
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<TableColumn> columns;

    /**
     *  其他信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Object> meta;

    public static <T> PageResult<T> build(Long pageNum, Long pageSize, Long total, List<T> data) {
        final PageResult pageR = new PageResult();
        pageR.setList(data);
        if(pageSize != null && pageSize >= 0){
            final PageInfo pageInfo = new PageInfo();
            pageInfo.setPageNum(pageNum);
            pageInfo.setPageSize(pageSize);
            pageInfo.setTotal(total);
            pageR.setPageInfo(pageInfo);
        }
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

    public void putMeta(final String key, Object val){
        if(StrUtil.isBlank(key)){
            return;
        }
        if(meta == null){
            meta = new LinkedHashMap<>();
        }
        meta.put(key, val);
    }
}
