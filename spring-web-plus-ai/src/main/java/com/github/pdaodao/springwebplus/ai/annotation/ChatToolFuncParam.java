package com.github.pdaodao.springwebplus.ai.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({PARAMETER})
public @interface ChatToolFuncParam {

    /**
     * 参数描述
     * @return
     */
    String value();

    /**
     * 是否必填
     * @return
     */
    boolean required() default true;
}
