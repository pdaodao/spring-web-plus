package com.github.pdaodao.springwebplus.base.pojo;

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
}
