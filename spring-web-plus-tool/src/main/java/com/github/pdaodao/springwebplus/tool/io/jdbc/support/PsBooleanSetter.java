package com.github.pdaodao.springwebplus.tool.io.jdbc.support;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Data
public class PsBooleanSetter implements PsSetter<Boolean>{
    private final String name;
    private final String from;

    public PsBooleanSetter(String name, String from) {
        this.name = name;
        if(StrUtil.isBlank(from)){
            from = name;
        }
        this.from = from;
    }

    @Override
    public Boolean set(PreparedStatement ps, int index, Object obj) throws SQLException {
        final Boolean b = DataValueUtil.toBoolean(obj);
        if(b == null){
            ps.setNull(index, Types.BOOLEAN);
        }else {
            ps.setBoolean(index, b);
        }
        return b;
    }
}
