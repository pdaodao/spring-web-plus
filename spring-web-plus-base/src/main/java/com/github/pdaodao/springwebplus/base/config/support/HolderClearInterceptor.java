package com.github.pdaodao.springwebplus.base.config.support;

import com.github.pdaodao.springwebplus.base.auth.LoginUtil;
import com.github.pdaodao.springwebplus.base.frame.ThreadLocalManager;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class HolderClearInterceptor implements HandlerInterceptor {
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestUtil.clearHolder();
        LoginUtil.clearHolder();
        PageHelper.clearHolder();
        FileStorage.clearHolder();
        ThreadLocalManager.clearHolder();
    }
}
