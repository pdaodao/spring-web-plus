package com.github.pdaodao.springwebplus.tool.io.jdbc.support;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Data
public class PsObjectSetter implements PsSetter<Object>{
    private final String name;
    private final String from;
    private final DataType dataType;

    public PsObjectSetter(String name, String from, DataType dataType) {
        this.name = name;
        if(StrUtil.isBlank(from)){
            from = name;
        }
        this.from = from;
        this.dataType = dataType;
    }

    @Override
    public Object set(PreparedStatement ps, int index, Object obj) throws SQLException {
        if(dataType != null){
            obj = DataValueUtil.toAs(obj, dataType);
        }
        ps.setObject(index, obj);
        return obj;
    }
}
