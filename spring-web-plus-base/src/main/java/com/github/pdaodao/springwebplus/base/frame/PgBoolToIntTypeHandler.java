package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

public class PgBoolToIntTypeHandler extends BaseTypeHandler<Boolean> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        if(parameter == null){
            return;
        }
        if(BooleanUtil.isTrue(parameter)){
            ps.setInt(i, 1);
            return;
        }
        ps.setInt(i, 0);
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int result = rs.getInt(columnName);
        if(0 == result && rs.wasNull()){
            return null;
        }
        if(ObjectUtil.equals(1, result)){
            return true;
        }
        return false;
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int result = rs.getInt(columnIndex);
        if(0 == result && rs.wasNull()){
            return null;
        }
        if(ObjectUtil.equals(1, result)){
            return true;
        }
        return false;
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int result = cs.getInt(columnIndex);
        if(0 == result && cs.wasNull()){
            return null;
        }
        if(ObjectUtil.equals(1, result)){
            return true;
        }
        return false;
    }
}
