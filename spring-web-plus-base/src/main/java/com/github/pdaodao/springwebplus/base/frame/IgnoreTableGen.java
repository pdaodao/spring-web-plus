package com.github.pdaodao.springwebplus.base.frame;

import java.lang.annotation.*;

/**
 * 忽略表结构自动创建
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface IgnoreTableGen {
}