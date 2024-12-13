package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.PropDesc;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.pdaodao.springwebplus.base.entity.DaoEntity;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.db.core.TableIndex;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.db.util.SqlUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.*;

public class EntityScanUtil {
    static final String PlusTableNameClass = "com.baomidou.mybatisplus.annotation.TableName";
    static final String XpTableClass = "javax.persistence.Table";


    /**
     * 扫描得到实体列表
     *
     * @return
     */
    public static List<TableInfo> entityList() {
        final Set<Class<?>> cls = ClassScanner.scanAllPackageBySuper("com.github.pdaodao", DaoEntity.class);
        final Set<Class<?>> apps = ClassScanner.scanAllPackageBySuper(SpringUtil.getBootScanPackage(), DaoEntity.class);
        final Set<Class<?>> all = new HashSet<>();
        all.addAll(cls);
        all.addAll(apps);

        final List<TableInfo> list = new ArrayList<>();
        for (final Class clazz : all) {
            final TableInfo info = toTableInfo(clazz);
            if (info == null) {
                continue;
            }
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
        if (clazz.isAnnotationPresent(IgnoreTableGen.class)) {
            return null;
        }
        if (!ClassUtil.isAssignable(DaoEntity.class, clazz)) {
            return null;
        }
        final TableInfo tableInfo = new TableInfo();
        tableInfo.setIndexList(new ArrayList<>());

        // 表名
        if (ClassLoaderUtil.isPresent(PlusTableNameClass)) {
            tableInfo.setName(MybatisPlusUtil.tableName(clazz));
        }
        if (StrUtil.isBlank(tableInfo.getName())) {
            if (ClassLoaderUtil.isPresent(XpTableClass)) {
                tableInfo.setName(XpTableUtil.tableName(clazz));
            }
        }
        if (StrUtil.isBlank(tableInfo.getName())) {
            tableInfo.setName(StrUtils.toUnderlineCase(clazz.getSimpleName()));
        }
        // 表注释
        if (clazz.isAnnotationPresent(Schema.class)) {
            final Schema schema = (Schema) clazz.getAnnotation(Schema.class);
            if (StrUtil.isNotBlank(schema.description())) {
                tableInfo.setRemark(schema.description());
                tableInfo.setTitle(schema.description());
            }
        }
        // 属性
        final BeanDesc beanDesc = BeanUtil.getBeanDesc(clazz);
        for (final PropDesc propDesc : beanDesc.getProps()) {
            final TableColumn columnInfo = toColumnInfo(propDesc, tableInfo.getIndexList());
            if (columnInfo == null) {
                continue;
            }
            tableInfo.addColumn(columnInfo);
        }
        sortFieldSeq(tableInfo);

        return tableInfo;
    }

    /**
     * 提取字段信息
     *
     * @param propDesc
     * @param indexList
     * @return
     */
    private static TableColumn toColumnInfo(final PropDesc propDesc, final List<TableIndex> indexList) {
        if (java.lang.reflect.Modifier.isTransient(propDesc.getField().getModifiers())) {
            return null;
        }
        final TableColumn ff = new TableColumn();
        ff.setIsAuto(false);
        ff.setIsPk(false);
        ff.setNullable(true);
        // 字段名称
        if (ClassLoaderUtil.isPresent(PlusTableNameClass)) {
            if (MybatisPlusUtil.isFieldIgnore(propDesc.getField())) {
                return null;
            }
            final String name = MybatisPlusUtil.tableField(propDesc.getField());
            if (StrUtil.isNotBlank(name)) {
                ff.setName(SqlUtil.dropSqlEscape(name));
            }
        }
        if (ClassLoaderUtil.isPresent(XpTableClass)) {
            if (XpTableUtil.isFieldIgnore(propDesc.getField())) {
                return null;
            }
            if (StrUtil.isBlank(ff.getName())) {
                final String name = XpTableUtil.tableField(propDesc.getField());
                ff.setName(SqlUtil.dropSqlEscape(name));
            }
        }
        if (StrUtil.isBlank(ff.getName())) {
            ff.setName(StrUtils.toUnderlineCase(propDesc.getFieldName()).trim().toLowerCase());
        }
        // remark
        final Schema apiModelProperty = propDesc.getField().getAnnotation(Schema.class);
        if (apiModelProperty != null && StrUtil.isNotBlank(apiModelProperty.description())) {
            ff.setRemark(apiModelProperty.description());
            ff.setTitle(ff.getRemark());
        }
        // 处理索引
        final TableFieldIndex tableFieldHelper = propDesc.getField().getAnnotation(TableFieldIndex.class);
        if (tableFieldHelper != null) {
            final TableIndex indexInfo = new TableIndex();
            if (StrUtil.isBlank(tableFieldHelper.fields())) {
                indexInfo.addColumn(ff.getName());
            } else {
                // 组合索引
                for (final String f : StrUtil.split(tableFieldHelper.fields(), ",")) {
                    indexInfo.addColumn(f);
                }
            }
            indexInfo.setIsUnique(tableFieldHelper.isUnique());
            if (StrUtil.isNotBlank(tableFieldHelper.indexName())) {
                indexInfo.setName(tableFieldHelper.indexName());
            }
            if (StrUtil.isEmpty(indexInfo.getName()) && CollUtil.isNotEmpty(indexInfo.getFields())) {
                indexInfo.setName(StrUtil.join("_", indexInfo.getFields()));
            }
            indexList.add(indexInfo);
        }

        // 处理字段类型相关
        // 主键字段
        if (propDesc.getField().isAnnotationPresent(TableId.class)) {
            ff.setIsPk(true);
            ff.setLength(32);
            if (XpTableUtil.fieldHasUuid(propDesc.getField())
                    || StrUtil.equals("String", propDesc.getField().getGenericType().getTypeName())
                    || propDesc.getFieldClass().equals(String.class)
                    || propDesc.getFieldClass().isEnum()) {
                // 字符串 或者枚举类型
                ff.setLength(36);
                ff.setTypeName("varchar");
                ff.setDataType(DataType.STRING);
            } else {
                ff.setLength(12);
                ff.setIsAuto(true);
                ff.setTypeName("bigint");
                ff.setDataType(DataType.BIGINT);
            }
            ff.setNullable(false);
            return ff;
        }
        final Integer maxLength = getFieldSize(propDesc, ff);

        // 字符串类型
        if (propDesc.getFieldClass().equals(String.class)) {
            ff.setDataType(DataType.STRING);
            ff.setTypeName("varchar");
            ff.setLength(50);
            if (propDesc.getFieldName().endsWith("id")) {
                ff.setLength(36);
            } else if (maxLength != null && maxLength > 0) {
                ff.setLength(maxLength);
                if (maxLength > 1000) {
                    ff.setDataType(DataType.TEXT);
                }
            } else {
                ff.setLength(255);
            }
            return ff;
        }
        if (propDesc.getFieldClass().equals(Boolean.class) || propDesc.getFieldClass().getName().equals("boolean")) {
            ff.setDataType(DataType.BOOLEAN);
            ff.setTypeName("boolean");
            return ff;
        }
        if (propDesc.getFieldClass().equals(Integer.class) || propDesc.getFieldClass().getName().equals("int")) {
            ff.setDataType(DataType.INT);
            ff.setTypeName("int");
            return ff;
        }
        //  长整数类型
        if (propDesc.getFieldClass().equals(Long.class) || propDesc.getFieldClass().getName().equals("long")) {
            ff.setDataType(DataType.BIGINT);
            ff.setTypeName("bigint");
            return ff;
        }
        if (propDesc.getFieldClass().equals(BigDecimal.class)) {
            ff.setDataType(DataType.DECIMAL);
            ff.setLength(11);
            ff.setScale(4);
            return ff;
        }

        if (propDesc.getFieldClass().equals(Float.class) || propDesc.getFieldClass().getName().equals("float")) {
            ff.setDataType(DataType.DOUBLE);
            ff.setTypeName("double");
            return ff;
        }

        if (propDesc.getFieldClass().equals(Double.class) || propDesc.getFieldClass().getName().equals("double")) {
            ff.setDataType(DataType.DOUBLE);
            ff.setTypeName("double");
            ff.setScale(8);
            ff.setLength(20);
            return ff;
        }

        if (propDesc.getFieldClass().equals(Date.class)) {
            ff.setDataType(DataType.DATETIME);
            ff.setTypeName("datetime");
            return ff;
        }
        // 枚举类型
        if (propDesc.getFieldClass().isEnum()) {
            ff.setIsAuto(false);
            if (maxLength != null) {
                ff.setLength(maxLength);
            }
            if (ff.getLength() == null) {
                ff.setLength(32);
            }
            ff.updateDataTypeIfNull(DataType.STRING);
            ff.setTypeName("varchar");
            return ff;
        }

        // pojo 类型
        if (!propDesc.getFieldClass().isPrimitive()) {
            ff.setIsAuto(false);
            ff.updateDataTypeIfNull(DataType.STRING);
            ff.setLength(maxLength);
            if (propDesc.getField().getType().getName().contains(".List")) {
                ff.setDataType(DataType.TEXT);
                ff.setLength(3500);
            }
            if (maxLength != null && maxLength > 1000) {
                ff.setDataType(DataType.TEXT);
            }
            if (ff.getLength() == null) {
                ff.setLength(3000);
            }
            return ff;
        }
        Preconditions.assertTrue(true, "unknows type {} for {}", propDesc.getFieldClass().getSimpleName(), propDesc.getFieldName());
        return null;
    }


    /**
     * 获取字段长度
     *
     * @param p
     * @param ff
     * @return
     */
    private static Integer getFieldSize(final PropDesc p, final TableColumn ff) {
        final TableFieldSize tableFieldSize = p.getField().getAnnotation(TableFieldSize.class);
        if (tableFieldSize != null) {
            if (StrUtil.isNotBlank(tableFieldSize.defaultValue())) {
                ff.setDefaultValue(tableFieldSize.defaultValue());
            }
            if (tableFieldSize.value() > 1) {
                return tableFieldSize.value();
            }
        }
        final Size size = p.getField().getAnnotation(Size.class);
        final Integer maxLength = size != null && size.max() > 0 ? size.max() : null;
        return maxLength;
    }

    /**
     * 由于类继承的问题 这里调整一下字段顺序
     *
     * @param tableInfo
     */
    private static void sortFieldSeq(final TableInfo tableInfo) {
        if (tableInfo == null || CollUtil.isEmpty(tableInfo.getColumns())) {
            return;
        }
        // 对这里的字段顺序重新排列一下
        CollUtil.sort(tableInfo.getColumns(), new Comparator<TableColumn>() {
            @Override
            public int compare(TableColumn o1, TableColumn o2) {
                if (o1.getName().equalsIgnoreCase("id")) {
                    return -1;
                }
                if (o2.getName().equalsIgnoreCase("id")) {
                    return 1;
                }
                if (o1.getName().startsWith("creator_")
                        || o1.getName().equalsIgnoreCase("is_deleted")
                        || o1.getName().startsWith("updator_")
                        || o1.getName().endsWith("_time")) {
                    return 1;
                }
                return 0;
            }
        });
    }
}
