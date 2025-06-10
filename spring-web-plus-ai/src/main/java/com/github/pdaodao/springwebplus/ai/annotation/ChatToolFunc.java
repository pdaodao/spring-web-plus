package com.github.pdaodao.springwebplus.ai.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD})
public @interface ChatToolFunc {
    /**
     * 函数名称 默认java函数名称
     * @return
     */
    String name() default "";

    String namespace() default "";

    /**
     * 功能描述
     * @return
     */
    String[] value() default "";
}