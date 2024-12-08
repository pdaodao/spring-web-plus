package com.github.pdaodao.springwebplus.tool.db.handler;

import cn.hutool.db.handler.RsHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RsLongHandler implements RsHandler<Long> {
    @Override
    public Long handle(ResultSet rs) throws SQLException {
        return rs.next() ? rs.getLong(1) : null;
    }
}
