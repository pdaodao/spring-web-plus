package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
public class TokenInfo {
    /**
     * token
     */
    public String token;
    /**
     * 登陆时间
     */
    private Date loginTime = new Date();

    /**
     * 上次使用时间
     */
    @JsonIgnoreProperties
    private Date lastAccessTime;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户登陆名
     */
    private String username;

    /**
     * 本次登陆的设备类型
     */
    private String device;

    /**
     * 指定此次登录 token 有效期，单位：秒 （如未指定，自动取全局配置的 timeout 值）
     */
    public Integer tokenTimeout;

    /**
     * 指定此次登录 token 最低活跃频率，单位：秒（如未指定，则使用全局配置的 activeTimeout 值）
     */
    private Integer tokenActiveTimeout;
}
