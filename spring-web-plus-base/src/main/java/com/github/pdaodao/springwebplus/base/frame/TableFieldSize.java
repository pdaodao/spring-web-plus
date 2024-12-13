package com.github.pdaodao.springwebplus.base.frame;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableFieldSize {
    int value() default 255;

    String defaultValue() default "";
}