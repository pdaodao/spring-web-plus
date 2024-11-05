package com.github.pdaodao.springwebplus.service;

import cn.hutool.http.HttpUtil;
import com.github.pdaodao.springwebplus.base.util.JsonUtil;
import com.github.pdaodao.springwebplus.config.WxMpProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WxMpApi {

    private static WxMpProperties wxMpProperties;

    public WxMpApi(WxMpProperties wxMpProperties) {
        log.info("wxMpProperties:" + wxMpProperties);
        WxMpApi.wxMpProperties = wxMpProperties;
    }

    /**
     * 微信小程序登录
     *
     * @param code
     * @return
     * @throws Exception
     */
    public static String getOpenid(String code) {
        log.info("微信小程序code：" + code);
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", wxMpProperties.getAppid());
        paramMap.put("secret", wxMpProperties.getSecret());
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");
        String result = HttpUtil.get(url, paramMap);
        log.info("微信小程序登录结果：" + result);
        final Map<String, String> jsonObject = JsonUtil.asMap(result);
        String openid = jsonObject.get("openid");
        log.info("微信小程序登录openid：" + openid);
        String errcode = jsonObject.get("errcode");
        String errmsg = jsonObject.get("errmsg");
        if (StringUtils.isBlank(openid)) {
            log.error("微信小程序登录失败,errcode:{},errmsg", errcode, errmsg);
            throw new IllegalArgumentException("微信小程序登录失败");
        }
        return openid;
    }

}
