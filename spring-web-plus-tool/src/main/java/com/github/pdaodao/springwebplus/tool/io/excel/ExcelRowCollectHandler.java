package com.github.pdaodao.springwebplus.tool.io.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.sax.handler.AbstractRowHandler;
import cn.hutool.poi.excel.sax.handler.RowHandler;

import java.util.List;

public class ExcelRowCollectHandler implements RowHandler {
    private final List<List<Object>> rows;

    public ExcelRowCollectHandler(List<List<Object>> rows) {
        this.rows = rows;
    }

    @Override
    public void handle(int i, long l, List<Object> list) {
        if(CollUtil.isEmpty(list)){
            return;
        }
        boolean has = false;
        for(final Object obj: list){
            if(ObjectUtil.isNotEmpty(obj)){
                has = true;
                break;
            }
        }
        if(has == false){
            return;
        }
        rows.add(list);
    }
}
