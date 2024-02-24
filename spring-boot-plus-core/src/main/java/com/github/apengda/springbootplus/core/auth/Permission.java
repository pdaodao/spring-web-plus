package com.github.apengda.springbootplus.core.auth;

import java.lang.annotation.*;

/**
 * 接口访问权限注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {

    /**
     * 权限编码
     *
     * @return
     */
    String value();

    /**
     * 角色编码
     *
     * @return
     */
    String role() default "";

    /**
     * 角色编码数组
     *
     * @return
     */
    String[] roles() default {};
}