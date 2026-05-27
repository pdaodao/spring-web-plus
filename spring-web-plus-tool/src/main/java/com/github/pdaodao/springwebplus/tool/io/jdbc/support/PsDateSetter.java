package com.github.pdaodao.springwebplus.tool.io.jdbc.support;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

@Data
public class PsDateSetter implements PsSetter<LocalDateTime> {
    private final String name;
    private final String from;

    public PsDateSetter(String name, String from) {
        this.name = name;
        if(StrUtil.isBlank(from)){
            from = name;
        }
        this.from = from;
    }

    @Override
    public LocalDateTime set(PreparedStatement ps, int index, Object obj) throws SQLException {
        final LocalDateTime ret = DataValueUtil.toDate(obj);
        if(ret == null){
            ps.setNull(index, Types.DATE);
        }else{
            ps.setTimestamp(index, Timestamp.valueOf(ret));
        }
        return ret;
    }
}
