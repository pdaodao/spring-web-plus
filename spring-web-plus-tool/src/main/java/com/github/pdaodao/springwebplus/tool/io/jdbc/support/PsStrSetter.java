package com.github.pdaodao.springwebplus.tool.io.jdbc.support;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

@Data
public class PsStrSetter implements PsSetter<String>{
    private final String name;
    private final String from;

    public PsStrSetter(String name, String from) {
        this.name = name;
        if(StrUtil.isBlank(from)){
            from = name;
        }
        this.from = from;
    }

    @Override
    public String set(PreparedStatement ps, int index, Object obj) throws SQLException {
        if(obj == null){
            ps.setNull(index, Types.VARCHAR);
            return null;
        }
        String ret = DataValueUtil.toString(obj, null);
        if(ret != null){
            ret = ret.replace("\u0000", " ");
        }
        ps.setString(index,  ret);
        return ret;
    }
}
