package com.github.pdaodao.springwebplus.tool.db.handler;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProcessor {

    void before(final Connection connection) throws SQLException;

    void after(final Connection connection) throws SQLException;
}
