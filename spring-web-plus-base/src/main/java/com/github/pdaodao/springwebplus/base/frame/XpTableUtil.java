package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.ObjectUtil;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class XpTableUtil {

    public static String tableName(final Class clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return null;
        }
        final Table tableName = (Table) clazz.getAnnotation(Table.class);
        return tableName.name();
    }

    public static String tableField(final Field field) {
        final Column tableField = field.getAnnotation(Column.class);
        if (tableField == null) {
            return null;
        }
        return tableField.name();
    }

    public static boolean isFieldIgnore(final Field field) {
        final Transient a = field.getAnnotation(Transient.class);
        return ObjectUtil.isNotNull(a);
    }

    public static boolean fieldHasUuid(final Field field) {
        for (Annotation an : field.getAnnotations()) {
            if (an.toString().contains(".Uuid")) {
                return true;
            }
        }
        return false;
    }

}
