package com.github.pdaodao.springwebplus.starter.auth;

import java.lang.annotation.*;

/**
 * 接口不需要登录
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreLogin {

}
