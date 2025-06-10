package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderByInfo {
    /**
     * 排序项目列表
     */
    private List<OrderItem> orderItemList;


    public void addItem(final String name, final Boolean asc){
        if(CollUtil.isEmpty(orderItemList)){
            orderItemList = new ArrayList<>();
        }
        orderItemList.add(new OrderItem(name, asc));
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        /**
         * 字段名称
         */
        private String name;

        /**
         * 是否升序 默认升序
         */
        private Boolean asc = true;
    }
}
