package com.github.pdaodao.springwebplus.base.frame;

import java.lang.annotation.*;

/**
 * 字段索引相关注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableFieldIndex {
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

    /**
     * 组合索引字段
     *
     * @return
     */
    String fields() default "";
}