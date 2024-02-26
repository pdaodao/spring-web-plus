package com.github.apengda.springwebplus.starter.config.support;

import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Configuration
public class CurrentUserInfoParamResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(CurrentUserInfo.class);
    }

    @Override
    public CurrentUserInfo resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        CurrentUserInfo user = new CurrentUserInfo();
// todo
//        String userId = RequestUtil.getUserIdString();
//        String username = RequestUtil.getUsername();
//        user.setUserId(userId);
//        user.setUsername(username);
        return user;
    }
}
