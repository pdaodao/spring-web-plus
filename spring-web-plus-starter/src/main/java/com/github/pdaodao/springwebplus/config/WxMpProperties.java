package com.github.pdaodao.springwebplus.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序属性配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.mp")
public class WxMpProperties {
    /**
     * appid
     */
    private String appid;

    /**
     * 密钥
     */
    private String secret;
}