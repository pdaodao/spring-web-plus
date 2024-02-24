package com.github.apengda.springbootplus.core.config.support;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.github.apengda.springbootplus.core.pojo.PageRequestParam;
import com.github.apengda.springbootplus.core.util.Preconditions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class PageRequestParamResolver implements HandlerMethodArgumentResolver {
    private static Integer validateNum(final String numStr, final String name, final Integer min, final Integer max) {
        Preconditions.checkNotEmpty(numStr, name + "值为空");
        Preconditions.checkArgument(NumberUtil.isInteger(numStr), name + "非法的数值:" + numStr);
        final Integer num = NumberUtil.parseInt(numStr);
        if (min != null) {
            Preconditions.checkArgument(num >= min, name + "最小值为:" + min);
        }
        if (max != null) {
            Preconditions.checkArgument(num <= max, name + "最大值为:" + max);
        }
        return num;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(PageRequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        final PageRequestParam pagerParams = new PageRequestParam();
        final HttpServletRequest httpRequest = (HttpServletRequest) nativeWebRequest
                .getNativeRequest();
        final String pageNum = httpRequest.getParameter("pageNum");
        if (StrUtil.isNotBlank(pageNum)) {
            pagerParams.setPageNum(validateNum(pageNum, "pageNum", 1, 1000000));
        }
        if (StrUtil.isNotBlank(httpRequest.getParameter("pageSize"))) {
            pagerParams.setPageSize(validateNum(httpRequest.getParameter("pageSize"), "pageSize", 1, 30000));
        }
        if (StrUtil.isNotBlank(httpRequest.getParameter("orderBy"))) {
            final String orderField = httpRequest.getParameter("orderBy").trim();
            Preconditions.assertTrue(orderField.contains(" "), "非法的排序:" + orderField);
            pagerParams.setOrderBy(orderField);
            if (StrUtil.isNotBlank(httpRequest.getParameter("orderAsc"))) {
                pagerParams.setOrderAsc(Boolean.parseBoolean(httpRequest.getParameter("orderAsc")));
            }
        }
        return pagerParams;
    }
}
