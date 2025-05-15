package com.github.pdaodao.springwebplus.base.frame;

import java.lang.annotation.*;

/**
 * 字段excel导出时 忽略
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ExcelIgnore {
    boolean value() default true;
}