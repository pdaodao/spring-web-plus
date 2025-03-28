package com.github.pdaodao.springwebplus.base.pojo.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberTypeListTypeHandler extends BaseTypeHandler<List<MemberType>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<MemberType> parameter, JdbcType jdbcType) throws SQLException {
        if(!CollUtil.isEmpty(parameter)) {
            ps.setString(i, StrUtil.join(",", parameter));
        }
    }

    @Override
    public List<MemberType> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toList(rs.getString(columnName));
    }

    @Override
    public List<MemberType> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toList(rs.getString(columnIndex));
    }

    @Override
    public List<MemberType> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
       return toList(cs.getString(columnIndex));
    }

    private static List<MemberType> toList(final String str) {
        if(StrUtil.isEmpty(str)){
            return ListUtil.empty();
        }
        final List<MemberType> ret = new ArrayList<>();
        for(final String t: StrUtil.split(str, ",")){
            ret.add(MemberType.valueOf(t));
        }
        return ret;
    }
}
