package com.github.pdaodao.springwebplus.base.pojo.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.PermissionOperate;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionOperateListTypeHandler extends BaseTypeHandler<List<PermissionOperate>> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<PermissionOperate> parameter, JdbcType jdbcType) throws SQLException {
        if(!CollUtil.isEmpty(parameter)) {
            ps.setString(i, StrUtil.join(",", parameter));
        }
    }

    @Override
    public List<PermissionOperate> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toList(rs.getString(columnName));
    }

    @Override
    public List<PermissionOperate> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toList(rs.getString(columnIndex));
    }

    @Override
    public List<PermissionOperate> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
       return toList(cs.getString(columnIndex));
    }

    private static List<PermissionOperate> toList(final String str) {
        if(StrUtil.isEmpty(str)){
            return ListUtil.empty();
        }
        final List<PermissionOperate> ret = new ArrayList<>();
        for(final String t: StrUtil.split(str, ",")){
            ret.add(PermissionOperate.valueOf(t));
        }
        return ret;
    }
}
