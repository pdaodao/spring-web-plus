package com.github.apengda.springbootplus.core.config;

import com.github.apengda.springbootplus.core.pojo.R;
import com.github.apengda.springbootplus.core.pojo.RestCode;
import com.github.apengda.springbootplus.core.util.JsonUtil;
import com.github.apengda.springbootplus.core.util.SpringUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 接口返回值封装
 */
@RestControllerAdvice
@ConditionalOnProperty(value = "global.restresponse", havingValue = "true", matchIfMissing = true)
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        final String typename = returnType.getGenericParameterType().getTypeName();
        if (typename.startsWith("org.springframework")) {
            return false;
        }
        final String name = returnType.getMember().getDeclaringClass().getName();
        return name.startsWith(SpringUtil.getBootPackage())
                || name.startsWith("com.github.apengda.springbootplus");
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable final Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null && returnType.getGenericParameterType().getTypeName().equals("void")) {
            return R.success(body);
        }
        final R restResponse = body instanceof R ? (R) body : R.success(body);
        if (restResponse.getStatus() == null) {
            restResponse.setStatus(RestCode.SUCCESS.code);
        }
        if (restResponse.getStatus() != null && restResponse.getStatus() < 600) {
            response.setStatusCode(HttpStatus.resolve(restResponse.getStatus()));
        }
        //因为handler处理类的返回类型是String，为了保证一致性，这里需要将ResponseResult转回去
        if (selectedConverterType.getName().equals(StringHttpMessageConverter.class.getName())) {
            return JsonUtil.toJsonString(restResponse);
        }
        return restResponse;
    }
}