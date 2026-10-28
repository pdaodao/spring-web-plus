package com.github.pdaodao.springwebplus.tool.db.handler;

import cn.hutool.db.handler.RsHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RsObjectHandler implements RsHandler<Object> {
    @Override
    public Object handle(ResultSet rs) throws SQLException {
        return rs.next() ? rs.getObject(1) : null;
    }
}
