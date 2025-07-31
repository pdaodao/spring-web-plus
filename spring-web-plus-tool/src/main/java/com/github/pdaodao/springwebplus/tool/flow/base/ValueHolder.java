package com.github.pdaodao.springwebplus.tool.flow.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValueHolder {
    private DataType datatype;

    // 引用类型 数据表id / ...
    private String ref;

    // 单个对象数据
    private TableRowData map;


    // 文件列表
    private List<InputStreamWrap> files;


    public static ValueHolder ofMap(final TableRowData map){
        final ValueHolder v = new ValueHolder();
        v.setDatatype(DataType.Object);
        v.setMap(map);
        return v;
    }

    public static ValueHolder ofFile(final List<InputStreamWrap> files){
        final ValueHolder v = new ValueHolder();
        v.setDatatype(DataType.Object);
        v.setFiles(files);
        return v;
    }
}
