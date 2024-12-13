package com.github.pdaodao.springwebplus.oauth2;

import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import com.github.pdaodao.springwebplus.dao.SysOauth2ClientDao;
import com.github.pdaodao.springwebplus.entity.SysOauth2Client;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
    private final SysOauth2ClientDao clientDao;

    @Override
    public SaClientModel getClientModel(String clientId) {
        final SysOauth2Client client = clientDao.byClientId(clientId);
        if (client == null) {
            return null;
        }
        final SaClientModel sa = new SaClientModel();
        sa.setClientId(client.getClientId());
        sa.setClientSecret(client.getClientSecret());
        sa.addAllowRedirectUris("*");
        sa.addContractScopes("openid", "userid", "userinfo", "oidc");
        sa.addAllowGrantTypes(     // 所有允许的授权模式
                GrantType.authorization_code, // 授权码式
                GrantType.implicit,  // 隐式式
                GrantType.refresh_token,  // 刷新令牌
                "phone_code"  // 自定义授权模式 手机号验证码登录
        );
        return sa;
    }

    // 根据 clientId 和 loginId 获取 openid
    @Override
    public String getOpenid(String clientId, Object loginId) {
        // 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
        return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
    }

}
