//package com.github.pdaodao.springwebplus.controller;
//
//import cn.dev33.satoken.context.SaHolder;
//import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
//import cn.dev33.satoken.oauth2.consts.GrantType;
//import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
//import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
//import cn.dev33.satoken.stp.StpUtil;
//import cn.dev33.satoken.util.SaFoxUtil;
//import cn.dev33.satoken.util.SaResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("oauth2")
//public class SaOAuth2ServerControllerTemp {
//
//    @RequestMapping("*")
//    public Object request() {
//        System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
//        return SaOAuth2ServerProcessor.instance
//                .dister();
//    }
//
//    // Sa-Token OAuth2 定制化配置
//    @Autowired
//    public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
//
//        // 添加 client 信息
//        oauth2Server.addClient(
//                new SaClientModel()
//                        .setClientId("1001")    // client id
//                        .setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
//                        .addAllowRedirectUris("*")    // 所有允许授权的 url
//                        .addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
//                        .addAllowGrantTypes(     // 所有允许的授权模式
//                                GrantType.authorization_code, // 授权码式
//                                GrantType.implicit,  // 隐式式
//                                GrantType.refresh_token,  // 刷新令牌
//                                GrantType.client_credentials  // 客户端模式
//                        )
//        );
//        // 可以添加更多 client 信息，只要保持 clientId 唯一就行了
//        // oauth2Server.addClient(...)
//
//        // 配置：未登录时返回的View
//        oauth2Server.notLoginView = () -> {
//            String msg = "当前会话在OAuth-Server端尚未登录，请先访问"
//                    + "<a href='/oauth2/doLogin?name=sa&pwd=123456' target='_blank'> doLogin登录 </a>"
//                    + "进行登录之后，刷新页面开始授权";
//            return msg;
//        };
//
//        // 配置：登录处理函数
//        oauth2Server.doLoginHandle = (name, pwd) -> {
//            if("sa".equals(name) && "123456".equals(pwd)) {
//                StpUtil.login(10001);
//                return SaResult.ok();
//            }
//            return SaResult.error("账号名或密码错误");
//        };
//
//        // 配置：确认授权时返回的 view
//        oauth2Server.confirmView = (clientId, scopes) -> {
//            String scopeStr = SaFoxUtil.convertListToString(scopes);
//            String yesCode =
//                    "fetch('/oauth2/doConfirm?client_id=" + clientId + "&scope=" + scopeStr + "', {method: 'POST'})" +
//                            ".then(res => res.json())" +
//                            ".then(res => location.reload())";
//            String res = "<p>应用 " + clientId + " 请求授权：" + scopeStr + "，是否同意？</p>"
//                    + "<p>" +
//                    "        <button onclick=\"" + yesCode + "\">同意</button>" +
//                    "        <button onclick='history.back()'>拒绝</button>" +
//                    "</p>";
//            return res;
//        };
//    }
//}
