package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.ColumnInfo;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeName;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseDataTypeConverter implements DataTypeConverter {

    @Override
    public String fieldDDL(final ColumnInfo columnInfo, final DDLBuildContext context) {
        Preconditions.checkNotNull(columnInfo.getDataType(), "field {} data-type is null.", columnInfo.getName());

        final StringBuilder ret = new StringBuilder();
        ret.append(columnInfo.getName()).append(" ");

        // 字段类型
        final FieldTypeName fieldTypeName = fieldTypeDDL(columnInfo);

        ret.append(fieldTypeName.getTypeName());
        // 是否为空
        if (ObjectUtil.equals(false, columnInfo.isNullable())) {
            ret.append(" NOT NULL");
        }
        // 自增
        if (ObjectUtil.equals(true, columnInfo.isAutoIncrement())) {
            final String autoStr = genDDLFieldAutoIncrement(columnInfo, fieldTypeName, context);
            if (StrUtil.isNotBlank(autoStr)) {
                ret.append(" ").append(autoStr);
            }
        }
        // 默认值
        if (StrUtil.isNotBlank(fieldTypeName.getColumnDef())) {
            ret.append(" DEFAULT ").append(fieldTypeName.getColumnDef());
        }
        // 备注
        final String remark = columnInfo.getComment();
        if (StrUtil.isNotBlank(remark)) {
            final String ct = genDDLFieldComment(columnInfo, context);
            if (StrUtil.isNotBlank(ct)) {
                ret.append(" ").append(ct);
            }
        }
        return ret.toString();
    }

    /**
     * 自增语句
     *
     * @return
     */
    protected String genDDLFieldAutoIncrement(ColumnInfo tableColumn, FieldTypeName typeWithDefault, final DDLBuildContext context) {
        return null;
    }

    /**
     * 字段注释
     *
     * @param field
     * @param context
     * @return
     */
    protected String genDDLFieldComment(ColumnInfo field, DDLBuildContext context) {
        return StrUtil.format(" COMMENT '{}' ", StrUtils.clean(field.getComment(), 60));
    }


    public FieldTypeName fieldTypeDDL(final ColumnInfo columnInfo) {
        Preconditions.checkNotNull(columnInfo.getDataType(), "colunm [{}] data-type is null.", columnInfo.getName());
        final DataType dataType = columnInfo.getDataType();
        // 布尔
        if (DataType.BOOLEAN == dataType) {
            return fieldDDLBool(columnInfo);
        }
        // json 类型
        if (DataType.MAP == dataType || DataType.ARRAY == dataType) {
            return fieldDDLJson(columnInfo);
        }
        // 二进制类型
        if (DataType.BINARY == dataType || DataType.FILE == dataType) {
            return fieldDDLBinary(columnInfo);
        }
        // 整数类型
        if (dataType.isIntFamily()) {
            return fieldDDLInt(columnInfo);
        }
        // 小数族
        if (dataType.isDoubleFamily()) {
            return fieldDDLDouble(columnInfo);
        }
        // 时间日期族
        if (dataType.isDateFamily()) {
            return fieldDDLDate(columnInfo);
        }
        // 字符串类型
        if (dataType.isStringFamily()) {
            return fieldDDLStr(columnInfo);
        }
        return null;
    }

    public FieldTypeName fieldDDLDate(final ColumnInfo columnInfo) {
        final FieldTypeName ret = FieldTypeName.of("datetime", columnInfo.getColumnDef());
        if (DataType.TIMESTAMP == columnInfo.getDataType()) {
            ret.setTypeName("timestamp");
            return ret;
        }
        if (DataType.DATE == columnInfo.getDataType()) {
            ret.setTypeName("date");
            return ret;
        }
        if (DataType.TIME == columnInfo.getDataType()) {
            ret.setTypeName("time");
            return ret;
        }
        return ret;
    }

    /**
     * 小数类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLDouble(final ColumnInfo columnInfo) {
        if (DataType.DECIMAL == columnInfo.getDataType()) {
            if (columnInfo.getDigit() != null && columnInfo.getDigit() > 0) {
                long length = columnInfo.getSize() > 0 ? columnInfo.getSize() : 20;
                final String type = StrUtil.format("decimal({},{})", length, columnInfo.getDigit());
                return FieldTypeName.of(type, columnInfo.getColumnDef());
            }
        }
        if (StrUtil.contains(columnInfo.getComment(), "金额")) {
            return FieldTypeName.of("decimal(20,6)", columnInfo.getColumnDef());
        }
        return FieldTypeName.of("double", columnInfo.getColumnDef());
    }

    /**
     * 整数类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLInt(final ColumnInfo columnInfo) {
        if (DataType.BIGINT == columnInfo.getDataType()) {
            return FieldTypeName.of("bigint", columnInfo.getColumnDef());
        }
        if (StrUtil.isNotBlank(columnInfo.getTypeName()) && columnInfo.getTypeName().toLowerCase().contains("small")) {
            return FieldTypeName.of("smallint", columnInfo.getColumnDef());
        }
        return FieldTypeName.of("int", columnInfo.getColumnDef());
    }

    /**
     * 二进制类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLBinary(final ColumnInfo columnInfo) {
        return FieldTypeName.of("LONGBLOB", null);
    }


    /**
     * json类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLJson(final ColumnInfo columnInfo) {
        return FieldTypeName.of("text", columnInfo.getColumnDef());
    }

    /**
     * 布尔类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLBool(final ColumnInfo columnInfo) {
        String df = columnInfo.getColumnDef();
        if (StrUtil.isNotBlank(df)) {
            if (df.equalsIgnoreCase("b'0'")) {
                df = "0";
            }
            if (df.equalsIgnoreCase("b'1'")) {
                df = "1";
            }
            if ("false".equalsIgnoreCase(df)) {
                df = "0";
            }
            if ("true".equalsIgnoreCase(df)) {
                df = "1";
            }
        }
        return FieldTypeName.of("tinyint", df);
    }


    /**
     * 字符串类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeName fieldDDLStr(final ColumnInfo columnInfo) {
        if (columnInfo.getSize() == 0 || columnInfo.getSize() > 500) {
            return FieldTypeName.of("text", columnInfo.getColumnDef());
        }
        long length = columnInfo.getSize();
        return FieldTypeName.of("varchar(" + length + ")", columnInfo.getColumnDef());
    }

    @Override
    public DataType toUniType(ColumnInfo columnInfo) {
        if (StrUtil.isBlank(columnInfo.getTypeName())) {
            return null;
        }
        final String dbType = columnInfo.getTypeName().trim().toLowerCase();
        if (dbType.contains("int") && dbType.contains("unsigned")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("numeric") || dbType.contains("decimal") || dbType.equals("dec")) {
            return DataType.DECIMAL;
        }
        if (dbType.contains("text") || dbType.contains("json") || dbType.contains("clob")) {
            return DataType.TEXT;
        }
        if (dbType.contains("double") || dbType.contains("float")) {
            return DataType.DOUBLE;
        }
        if (dbType.contains("boolean") || dbType.contains("bool")
                || dbType.contains("tinyint") || dbType.contains("bit")) {
            return DataType.BOOLEAN;
        }
        if (dbType.contains("timestamp")) {
            return DataType.TIMESTAMP;
        }
        if (dbType.contains("datetime")) {
            return DataType.DATETIME;
        }
        if (dbType.contains("date")) {
            return DataType.DATE;
        }
        if (dbType.contains("time")) {
            return DataType.TIME;
        }
        if (dbType.contains("char") || dbType.contains("varchar") || dbType.contains("string")) {
            return DataType.STRING;
        }
        if (dbType.contains("binary") || dbType.contains("blob")) {
            return DataType.BINARY;
        }
        if (dbType.contains("bigint") || dbType.contains("long") || dbType.contains("bigserial")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("int") || dbType.contains("serial")) {
            return DataType.INT;
        }
        if (dbType.contains("json")) {
            return DataType.MAP;
        }
        log.warn("unknown data type {} for {}", columnInfo.getTypeName(), columnInfo.getName());
        return DataType.UNKNOWN;
    }
}
