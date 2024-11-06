package com.github.pdaodao.springwebplus.base.frame;

import com.github.pdaodao.springwebplus.tool.data.DataType;

import java.lang.annotation.*;

/**
 * 字段索引相关注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableFieldHelper {
    /**
     * 字段长度
     *
     * @return
     */
    int size() default 0;


    /**
     * 是否是索引
     *
     * @return
     */
    boolean isIndex() default true;

    /**
     * 索引名称 名称相同的多个字段合并为组合索引
     *
     * @return
     */
    String indexName() default "";

    /**
     * 是否是唯一索引
     *
     * @return
     */
    boolean isUnique() default false;

    DataType dataType() default DataType.UNKNOWN;

    /**
     * 组合索引字段
     *
     * @return
     */
    String fields() default "";

    /**
     * 该字段以前是叫什么名称
     *
     * @return
     */
    String froms() default "";
}