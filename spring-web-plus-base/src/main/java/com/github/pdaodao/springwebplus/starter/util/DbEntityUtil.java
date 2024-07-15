package com.github.pdaodao.springwebplus.starter.util;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.tool.db.pojo.ColumnInfo;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.pojo.TableInfo;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;

import java.util.*;

public class DbEntityUtil {

    /**
     * 扫描得到实体列表
     *
     * @return
     */
    public static List<TableInfo> entityList() {
        final Set<Class<?>> cls = ClassScanner.scanAllPackageByAnnotation("com.github.pdaodao", TableName.class);
        final Set<Class<?>> apps = ClassScanner.scanAllPackageByAnnotation(SpringUtil.getBootPackage(), TableName.class);
        final Set<Class<?>> all = new HashSet<>();
        all.addAll(cls);
        all.addAll(apps);

        final List<TableInfo> list = new ArrayList<>();
        for (final Class clazz : all) {
            final TableInfo info = toTableInfo(clazz);
            list.add(info);
        }
        return list;
    }

    /**
     * 从类中提取表结构信息
     *
     * @param clazz
     * @return
     */
    public static TableInfo toTableInfo(final Class clazz) {
        if (ClassUtil.isAbstractOrInterface(clazz)) {
            return null;
        }
        if (!clazz.isAnnotationPresent(TableName.class)) {
            return null;
        }
        // 表名
        final TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
        String name;
        if (StrUtil.isNotBlank(tableName.value())) {
            name = tableName.value();
        } else {
            name = CharSequenceUtil.toUnderlineCase(clazz.getSimpleName());
        }
        final TableInfo tableInfo = new TableInfo(name);
        // 表注释
        if (clazz.isAnnotationPresent(Schema.class)) {
            final Schema schema = (Schema) clazz.getAnnotation(Schema.class);
            if (StrUtil.isNotBlank(schema.description())) {
                tableInfo.setComment(schema.description());
            }
        }
        // 属性
        final BeanDesc beanDesc = BeanUtil.getBeanDesc(clazz);
        final List<ColumnInfo> fields = new ArrayList<>();
        for (final PropDesc propDesc : beanDesc.getProps()) {
            final ColumnInfo columnInfo = toColumnInfo(propDesc, name);
            if (columnInfo == null) {
                continue;
            }
            if (columnInfo.getName().equals("id")) {
                fields.add(0, columnInfo);
            } else {
                fields.add(columnInfo);
            }
        }
        fields.forEach(t -> tableInfo.setColumn(t));
        // TODO 索引

        return tableInfo;
    }

    /**
     * 提取字段信息
     *
     * @param propDesc
     * @param tableName
     * @return
     */
    private static ColumnInfo toColumnInfo(final PropDesc propDesc, final String tableName) {
        if (java.lang.reflect.Modifier.isTransient(propDesc.getField().getModifiers())) {
            return null;
        }
        final ColumnInfo ff = new ColumnInfo();
        ff.setNullable(true);
        ff.setAutoIncrement(false);
        ff.setPk(false);
        // 字段名称
        final TableField tableField = propDesc.getField().getAnnotation(TableField.class);
        if (tableField != null && StrUtil.isNotBlank(tableField.value())) {
            ff.setName(tableField.value().toLowerCase().trim());
        } else {
            ff.setName(CharSequenceUtil.toUnderlineCase(propDesc.getFieldName()).trim().toLowerCase());
        }
        // 备注
        final Schema schema = propDesc.getField().getAnnotation(Schema.class);
        if (schema != null && StrUtil.isNotBlank(schema.description())) {
            ff.setComment(schema.description());
        }
        final TableId tableId = propDesc.getField().getAnnotation(TableId.class);
        if (tableId != null) {
            ff.setPk(true);
            ff.setNullable(false);
            if (tableId.type() == IdType.AUTO) {
                ff.setAutoIncrement(true);
                ff.setSize(12);
            } else {
                ff.setSize(32);
            }
        }
        // 字段长度
        final Length size = propDesc.getField().getAnnotation(Length.class);
        if (size != null && size.max() > 1) {
            ff.setSize(size.max());
        }
        // 字符串类型
        if (propDesc.getFieldClass().equals(String.class)) {
            ff.setDataType(DataType.STRING);
            ff.setTypeName("varchar");
            if (ff.getSize() < 1) {
                ff.setSize(255);
            }
            if (ff.getSize() > 1000) {
                ff.setDataType(DataType.TEXT);
                ff.setTypeName("text");
            }
            return ff;
        }
        // 布尔
        if (propDesc.getFieldClass().equals(Boolean.class)) {
            ff.setDataType(DataType.BOOLEAN);
            ff.setTypeName("boolean");
            ff.setSize(1);
            return ff;
        }
        // 整数
        if (propDesc.getFieldClass().equals(Integer.class)) {
            ff.setDataType(DataType.INT);
            ff.setTypeName("int");
            return ff;
        }
        //  长整数类型
        if (propDesc.getFieldClass().equals(Long.class)) {
            ff.setDataType(DataType.BIGINT);
            ff.setTypeName("bigint");
            return ff;
        }

        if (propDesc.getFieldClass().equals(Float.class)) {
            ff.setDataType(DataType.DOUBLE);
            ff.setTypeName("double");
            return ff;
        }

        if (propDesc.getFieldClass().equals(Double.class)) {
            ff.setDataType(DataType.DECIMAL);
            ff.setTypeName("decimal");
            ff.setDigit(6);
            ff.setSize(20);
            return ff;
        }

        if (propDesc.getFieldClass().equals(Date.class)) {
            ff.setDataType(DataType.DATETIME);
            ff.setTypeName("datetime");
            return ff;
        }
        // pojo 类型
        if (!propDesc.getFieldClass().isPrimitive()) {
            ff.setDataType(DataType.TEXT);
            ff.setTypeName("text");
            return ff;
        }
        Preconditions.assertTrue(true, "unknown data-type for field {} of table {}", ff.getName(), tableName);
        return ff;
    }

}
