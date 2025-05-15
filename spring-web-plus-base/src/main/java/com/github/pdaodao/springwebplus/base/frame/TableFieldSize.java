package com.github.pdaodao.springwebplus.base.frame;

import com.github.pdaodao.springwebplus.tool.data.DataType;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableFieldSize {
    int value() default 255;


    String defaultValue() default "";

    /**
     * 字典项编码
     * @return
     */
    String dic() default  "";

    /**
     * 单独配置字段类型
     * @return
     */
    DataType type() default DataType.UNKNOWN;
}