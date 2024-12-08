package com.github.pdaodao.springwebplus.tool.db.handler;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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
}
