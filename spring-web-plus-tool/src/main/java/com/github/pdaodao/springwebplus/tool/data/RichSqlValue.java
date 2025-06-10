package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RichSqlValue {
    // 类型
    private ValueType type;
    // 数据类型
    private DataType dataType;
    // 内容
    private String content;
    // 引用节点id
    private String refNodeId;
    // 显示标题
    private String title;

    // 子项列表，in 的数据也存储在此
    private List<RichSqlValue> items;

    public Object contentAs(){
        if(StrUtil.isBlank(content)){
            return null;
        }
        return DataValueUtil.toAs(content, dataType);
    }
}
