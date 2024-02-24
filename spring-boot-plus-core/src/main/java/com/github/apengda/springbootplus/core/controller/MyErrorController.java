package com.github.apengda.springbootplus.core.controller;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springbootplus.core.pojo.R;
import com.github.apengda.springbootplus.core.pojo.RestCode;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Hidden
@Controller
@RequestMapping("/error")
public class MyErrorController implements ErrorController {
    public static final String[] staticPrefix = {"/assets", "/public", "/static"};


    @RequestMapping
    @ResponseBody
    public R handleError(final HttpServletRequest request, final HttpServletResponse response,
                         final ModelAndView modelAndView) {
        final String errorMsg = Objects.toString(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        final String url = Objects.toString(request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH));
        final RestCode code = "Not Found".equals(errorMsg) ? RestCode.NOT_FOUND : RestCode.INTERNAL_SERVER_ERROR;
        final R restResponse = R.error(code);
        restResponse.setPath(url);
        if (StrUtil.isNotBlank(errorMsg)) {
            restResponse.setMsg(errorMsg);
        }
        restResponse.setTrace(errorMsg);
        return restResponse;
    }


    @RequestMapping(produces = {"text/html"})
    public Object handleHtml(final HttpServletRequest request,
                             final ModelAndView modelAndView) {
        final String errorMsg = Objects.toString(request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        final String url = Objects.toString(request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH));
        final int status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) == null ?
                0 : (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (!("Not Found".equals(errorMsg) || 404 == status)) {
            modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            if ("null".equals(url)) {
                modelAndView.setStatus(HttpStatus.NOT_FOUND);
            }
            modelAndView.setViewName("error.html");
            return modelAndView;
        }
        modelAndView.setStatus(HttpStatus.OK);
        // 静态资源 .js .css 等
        // 由于history 模式 路径可能为 /abc/static/1.js
        if (url.contains(".") || "/index.html".equals(url)) {
            int index = -1;
            for (final String st : staticPrefix) {
                index = url.indexOf(st);
                if (index > 1) {
                    break;
                }
            }
            // 资源不存在
            if (index < 1) {
                modelAndView.setStatus(HttpStatus.NOT_FOUND);
                return modelAndView;
            }
            final String forward = "forward:" + url.substring(index);
            modelAndView.setViewName(forward);
            return modelAndView;
        }
        final String forward = "forward:/index.html";
        modelAndView.setViewName(forward);
        return modelAndView;
    }
}

