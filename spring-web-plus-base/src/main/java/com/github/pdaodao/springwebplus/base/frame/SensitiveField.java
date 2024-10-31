package com.github.pdaodao.springwebplus.base.frame;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveFieldSerialize.class)
public @interface SensitiveField {

    /**
     * 敏感信息类型
     */
    SensitiveType value() default SensitiveType.OTHER;

    /**
     * 敏感类型为其他可用 front – 保留：前面的front位数；从1开始
     */
    int front() default 4;

    /**
     * 敏感类型为其他可用 end – 保留：后面的end位数；从1开始
     */
    int end() default 4;

    /**
     * 敏感信息
     *
     * @author xxm
     * @since 2021/10/25
     */
    enum SensitiveType {

        /**
         * 中文名
         */
        CHINESE_NAME,
        /**
         * 用户id
         */
        USER_ID,
        /**
         * 密码
         */
        PASSWORD,
        /**
         * 身份证号
         */
        ID_CARD,
        /**
         * 座机号
         */
        FIXED_PHONE,
        /**
         * 手机号
         */
        MOBILE_PHONE,
        /**
         * ip地址
         */
        IP,
        /**
         * 地址
         */
        ADDRESS,
        /**
         * 电子邮件
         */
        EMAIL,
        /**
         * 车牌号
         */
        CAR_LICENSE,
        /**
         * 银行卡
         */
        BANK_CARD,
        /**
         * 公司开户银行联号
         */
        CNAPS_CODE,

        /**
         * 其他
         */
        OTHER,

    }

}
