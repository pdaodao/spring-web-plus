package com.github.pdaodao.springwebplus.tool.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JdbcUtils {

    public static Object getResultSetValue(ResultSet rs, int index) throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            Blob blob = (Blob) obj;
            obj = blob.getBytes(1, (int) blob.length());
        } else if (obj instanceof Clob) {
            Clob clob = (Clob) obj;
            obj = clob.getSubString(1, (int) clob.length());
        } else if ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className)) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(index);
            if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        } else if (obj instanceof LocalDateTime) {
            obj = new Date(((LocalDateTime) obj).atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli());
        }
        return obj;
    }

    public static Long getGeneratedId(ResultSet resultSet) throws SQLException {
        if (resultSet.getObject("id") != null) {
            return Long.valueOf(resultSet.getObject("id").toString());
        } else if (resultSet.getObject("ID") != null) {
            return Long.valueOf(resultSet.getObject("ID").toString());
        } else if (resultSet.getObject("GENERATED_KEY") != null) {
            return Long.valueOf(resultSet.getObject("GENERATED_KEY").toString());
        }
        return null;
    }

    public static List<Long> getGeneratedKeys(final PreparedStatement ps) throws SQLException{
        final List<Long> list = new ArrayList<>();
        try (ResultSet resultSet = ps.getGeneratedKeys()) {
            while (resultSet.next()) {
                final Long id = getGeneratedId(resultSet);
                list.add(id);
            }
        }
        return list;
    }

}
