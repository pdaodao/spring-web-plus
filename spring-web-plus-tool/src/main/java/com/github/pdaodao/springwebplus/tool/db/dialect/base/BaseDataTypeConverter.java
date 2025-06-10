package com.github.pdaodao.springwebplus.tool.db.dialect.base;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.db.dialect.DataTypeConverter;
import com.github.pdaodao.springwebplus.tool.db.pojo.DDLBuildContext;
import com.github.pdaodao.springwebplus.tool.db.pojo.FieldTypeNameWrap;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseDataTypeConverter implements DataTypeConverter {

    @Override
    public String fieldDDL(final TableField from, final TableField columnInfo, final DDLBuildContext context) {
        Preconditions.checkNotNull(columnInfo.getDataType(), "field {} data-type is null.", columnInfo.getName());

        final StringBuilder ret = new StringBuilder();
        ret.append(columnInfo.getName()).append(" ");

        // 字段类型
        final FieldTypeNameWrap fieldTypeName = fieldTypeDDL(columnInfo);
        String fieldAutoSuffix = null;
        // 自增
        if (BooleanUtil.isTrue(columnInfo.getIsAuto())) {
            final String autoStr = genDDLFieldAutoIncrement(columnInfo, fieldTypeName, context);
            if (StrUtil.isNotBlank(autoStr)) {
                fieldAutoSuffix = " " + autoStr;
            }
        }
        if (BooleanUtil.isTrue(context.isModifyColumn)) {
            ret.append(modifyColumnTypePrefix());
        }
        ret.append(fieldTypeName.getTypeName());
        // 是否为空
        if (ObjectUtil.equals(false, columnInfo.getNullable()) &&
                (from == null || ObjectUtil.notEqual(columnInfo.getNullable(), from.getNullable()))) {
            ret.append(" NOT NULL");
        }
        if (StrUtil.isNotBlank(fieldAutoSuffix)) {
            ret.append(fieldAutoSuffix);
        }
        // 默认值
        if (!BooleanUtil.isTrue(columnInfo.getIsAuto()) && StrUtil.isNotBlank(fieldTypeName.getColumnDef()) &&
                (from == null || !StrUtil.equals(from.getDefaultValue(), columnInfo.getDefaultValue()))) {
            ret.append(" DEFAULT ").append(fieldTypeName.getColumnDef());
        }
        // 备注
        final String remark = columnInfo.getRemark();
        if (StrUtil.isNotBlank(remark)) {
            final String ct = genDDLFieldComment(from, columnInfo, context);
            if (StrUtil.isNotBlank(ct)) {
                ret.append(" ").append(ct);
            }
        }
        return ret.toString();
    }

    protected String modifyColumnTypePrefix() {
        return "";
    }

    /**
     * 自增语句
     *
     * @return
     */
    protected String genDDLFieldAutoIncrement(TableField tableColumn, FieldTypeNameWrap typeWithDefault, final DDLBuildContext context) {
        return null;
    }

    /**
     * 字段注释
     *
     * @param from
     * @param field
     * @param context
     * @return
     */
    protected String genDDLFieldComment(final TableField from, TableField field, DDLBuildContext context) {
        return StrUtil.format(" COMMENT '{}' ", StrUtils.clean(field.getRemark(), 60));
    }


    public FieldTypeNameWrap fieldTypeDDL(final TableField columnInfo) {
        Preconditions.checkNotNull(columnInfo.getDataType(), "colunm [{}] data-type is null.", columnInfo.getName());
        final DataType dataType = columnInfo.getDataType();
        // 布尔
        if (DataType.BOOLEAN == dataType) {
            return fieldDDLBool(columnInfo);
        }
        // json 类型
        if (DataType.Object == dataType || DataType.ARRAY == dataType) {
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

    public FieldTypeNameWrap fieldDDLDate(final TableField columnInfo) {
        final FieldTypeNameWrap ret = FieldTypeNameWrap.of("datetime", columnInfo.getDefaultValue());
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
    public FieldTypeNameWrap fieldDDLDouble(final TableField columnInfo) {
        if (DataType.DECIMAL == columnInfo.getDataType()) {
            if (columnInfo.getScale() != null && columnInfo.getScale() > 0) {
                long length = columnInfo.getLength() > 0 ? columnInfo.getLength() : 20;
                final String type = StrUtil.format("decimal({},{})", length, columnInfo.getScale());
                return FieldTypeNameWrap.of(type, columnInfo.getDefaultValue());
            }
        }
        if (StrUtil.contains(columnInfo.getRemark(), "金额")) {
            return FieldTypeNameWrap.of("decimal(20,6)", columnInfo.getDefaultValue());
        }
        return FieldTypeNameWrap.of("double", columnInfo.getDefaultValue());
    }

    /**
     * 整数类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLInt(final TableField columnInfo) {
        if (DataType.BIGINT == columnInfo.getDataType()) {
            return FieldTypeNameWrap.of("bigint", columnInfo.getDefaultValue());
        }
        if (StrUtil.isNotBlank(columnInfo.getTypeName()) && columnInfo.getTypeName().toLowerCase().contains("small")) {
            return FieldTypeNameWrap.of("smallint", columnInfo.getDefaultValue());
        }
        return FieldTypeNameWrap.of("int", columnInfo.getDefaultValue());
    }

    /**
     * 二进制类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLBinary(final TableField columnInfo) {
        return FieldTypeNameWrap.of("LONGBLOB", null);
    }


    /**
     * json类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLJson(final TableField columnInfo) {
        return FieldTypeNameWrap.of("text", columnInfo.getDefaultValue());
    }

    /**
     * 布尔类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLBool(final TableField columnInfo) {
        String df = columnInfo.getDefaultValue();
        if (StrUtil.isNotBlank(df)) {
            if (df.equalsIgnoreCase("b'0'")) {
                df = "false";
            }
            if (df.equalsIgnoreCase("b'1'")) {
                df = "true";
            }
            if ("0".equalsIgnoreCase(df)) {
                df = "false";
            }
            if ("1".equalsIgnoreCase(df)) {
                df = "true";
            }
        }
        return FieldTypeNameWrap.of("BOOLEAN", df);
    }


    /**
     * 字符串类型
     *
     * @param columnInfo
     * @return
     */
    public FieldTypeNameWrap fieldDDLStr(final TableField columnInfo) {
        if (columnInfo.getLength() == 0 || columnInfo.getLength() > 500) {
            return FieldTypeNameWrap.of("text", columnInfo.getDefaultValue());
        }
        long length = columnInfo.getLength();
        return FieldTypeNameWrap.of("varchar(" + length + ")", columnInfo.getDefaultValue());
    }

    @Override
    public DataType toUniType(TableField columnInfo) {
        if (StrUtil.isBlank(columnInfo.getTypeName())) {
            return null;
        }
        final String dbType = columnInfo.getTypeName().trim().toLowerCase();
        if (dbType.contains("int") && dbType.contains("unsigned")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("int8")) {
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
        if("GEOMETRY".equalsIgnoreCase(dbType)){
            return DataType.GEOMETRY;
        }
        if (dbType.contains("bigint") || dbType.contains("long")
                || dbType.contains("serial")
                || dbType.contains("bigserial")) {
            return DataType.BIGINT;
        }
        if (dbType.contains("int")) {
            return DataType.INT;
        }
        if (dbType.contains("json")) {
            return DataType.Object;
        }
        if(dbType.startsWith("geo")){
            return DataType.STRING;
        }
        log.warn("unknown data type {} for {}", columnInfo.getTypeName(), columnInfo.getName());
        return DataType.UNKNOWN;
    }
}
