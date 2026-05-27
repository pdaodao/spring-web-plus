package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenInfo {
    /**
     * token
     */
    public String token;
    /**
     * 登陆时间
     */
    private LocalDateTime loginTime = DateTimeUtil.now();

    /**
     * 上次使用时间
     */
    @JsonIgnoreProperties
    private LocalDateTime lastAccessTime;

    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户登陆名
     */
    private String username;

    private String userNickname;

    // 头像
    private String avatar;


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
