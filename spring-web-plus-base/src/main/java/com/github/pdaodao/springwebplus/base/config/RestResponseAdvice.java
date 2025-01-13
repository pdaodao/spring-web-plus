package com.github.pdaodao.springwebplus.base.config;

import com.github.pdaodao.springwebplus.base.pojo.IResponse;
import com.github.pdaodao.springwebplus.base.pojo.RestCode;
import com.github.pdaodao.springwebplus.base.pojo.RestResponse;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 接口返回值封装
 */
@RestControllerAdvice
@ConditionalOnProperty(value = "global.restResponse", havingValue = "true", matchIfMissing = true)
public class RestResponseAdvice implements ResponseBodyAdvice<Object> {

    private static boolean isCollectionReturnType(MethodParameter methodParameter) {
        final Type returnType = methodParameter.getGenericParameterType();
        if (returnType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) returnType).getRawType();
            if (rawType instanceof Class<?>) {
                return Collection.class.isAssignableFrom((Class<?>) rawType);
            }
        }
        return false;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        final String typename = returnType.getGenericParameterType().getTypeName();
        if (typename.startsWith("org.springframework")) {
            return false;
        }
        final String name = returnType.getMember().getDeclaringClass().getName();
        if (name.startsWith("org.spring")) {
            return false;
        }
        return true;
//        return name.startsWith(SpringUtil.getBootPackage())
//                || name.startsWith("com.github.pdaodao.springwebplus");
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null && returnType.getGenericParameterType().getTypeName().equals("void")) {
            return RestResponse.success(body);
        }
        IResponse restResponse;
        if (body == null) {
            restResponse = RestResponse.success(null);
        } else {
            if (body instanceof IResponse) {
                restResponse = (IResponse) body;
            } else if (body instanceof PageResult<?>) {
                restResponse = RestResponse.success((PageResult) body);
            } else {
                restResponse = RestResponse.success(body);
            }
        }
        if (restResponse instanceof RestResponse) {
            RestResponse r = (RestResponse) restResponse;
            if (r.getCode() == null) {
                r.setCode(RestCode.SUCCESS.code);
            }
        }
        //因为handler处理类的返回类型是String，为了保证一致性，这里需要将ResponseResult转回去
        if (selectedConverterType.getName().equals(StringHttpMessageConverter.class.getName())) {
            return JsonUtil.toJsonString(restResponse);
        }
        return restResponse;
    }
}