package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Field;

public class MybatisPlusUtil {

    public static String tableName(final Class clazz) {
        if (!clazz.isAnnotationPresent(TableName.class)) {
            return null;
        }
        final TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
        return tableName.value();
    }

    public static String tableField(final Field field) {
        final TableField tableField = field.getAnnotation(TableField.class);
        if (tableField == null) {
            return null;
        }
        if (ObjectUtil.equal(false, tableField.exist())) {
            return null;
        }
        return tableField.value();
    }

    public static boolean isFieldIgnore(final Field field) {
        final TableField tableField = field.getAnnotation(TableField.class);
        return tableField != null && !tableField.exist();
    }
}
