package com.github.pdaodao.springwebplus.base.support.entity;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.pojo.TokenInfo;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user_token")
public class SysUserTokenEntity extends SnowIdWithTimeEntity {
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

    public TokenInfo toTokenInfo(){
        final TokenInfo t = new TokenInfo();
        BeanUtil.copyProperties(this, t);
        t.setToken(getId());
        return t;
    }
}
