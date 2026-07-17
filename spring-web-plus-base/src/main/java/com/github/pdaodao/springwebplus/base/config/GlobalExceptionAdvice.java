package com.github.pdaodao.springwebplus.base.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.config.support.WebappFile;
import com.github.pdaodao.springwebplus.base.pojo.RestCode;
import com.github.pdaodao.springwebplus.base.pojo.RestException;
import com.github.pdaodao.springwebplus.base.pojo.RestResponse;
import com.github.pdaodao.springwebplus.base.service.SysRequestErrorLogService;
import com.github.pdaodao.springwebplus.base.support.SysRequestErrorLog;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.base.util.I18nUtil;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Optional;


@RestControllerAdvice
@Slf4j
@ConditionalOnProperty(value = "global.exception", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
public class GlobalExceptionAdvice {
    private final Optional<SysRequestErrorLogService> logService;
    private final SysConfigProperties configProperties;

    @ExceptionHandler(RestException.class)
    public RestResponse restCodeException(RestException e, HttpServletRequest request, HttpServletResponse response) {
        final RestCode restCode = e != null ? e.getCode() : RestCode.INTERNAL_SERVER_ERROR;
        if(ObjectUtil.equals(401, restCode.getCode())){
            e.setData(configProperties.getLoginUrl());
        }
        response.setStatus(200);
        return error(restCode, e.getData(), request, e);
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public RestResponse handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return error(RestCode.NOT_ACCEPTABLE, null, request, new Exception(message));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        return error(RestCode.NOT_ACCEPTABLE, null, request, RestException.invalidParam(processBindingResult(e.getBindingResult())));
    }

    @ExceptionHandler(RuntimeException.class)
    public RestResponse handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        return error(RestCode.INTERNAL_SERVER_ERROR, null, request, e);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public RestResponse handleServletRequestBindingException(ServletRequestBindingException e, HttpServletRequest request){
        return error(RestCode.NOT_ACCEPTABLE, null, request, e);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public RestResponse handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        return error(RestCode.INTERNAL_SERVER_ERROR, null, request, e);
    }

    @ExceptionHandler(value = {NoResourceFoundException.class}, produces = {"text/html"})
    public Object handleNoHandlerFoundException(final NoResourceFoundException ex, final HttpServletRequest request,
                                                final HttpServletResponse response) {
        final ModelAndView modelAndView = new ModelAndView();
        final String url = request.getServletPath();
        if(url.endsWith(".css") || url.endsWith(".js")){
            modelAndView.setStatus(HttpStatus.NOT_FOUND);
            return modelAndView;
        }
        modelAndView.setStatus(HttpStatus.OK);
        if(url.contains("/api/") || url.endsWith("index.html") || url.contains("/v1/") || url.contains("/v2/")){
            response.setStatus(404);
            response.setContentType("application/json");
            final RestResponse<String> restResponse = new RestResponse();
            restResponse.setCode(404);
            restResponse.setData(url);
            restResponse.setMsg(I18nUtil.getMessage("sys.not_found_short"));
            return restResponse;
        }
        if(url.replaceAll("/", "").length() <= url.length() -2){
            for(final String app: WebappFile.SubApps){
                if(url.startsWith("/"+app.trim().toLowerCase())){
                    final String forward = "forward:/"+app.trim().toLowerCase()+"/index.html";
                    modelAndView.setViewName(forward);
                    return modelAndView;
                }
            }
        }
        final String forward = "forward:/index.html";
        modelAndView.setViewName(forward);
        return modelAndView;
    }


    private String processBindingResult(BindingResult bindingResult) {
        StringBuilder sb = new StringBuilder();
        if (bindingResult != null) {
            bindingResult.getFieldErrors()
                    .forEach(t -> {
                        sb.append(t.getField()).append(":").append(t.getDefaultMessage());
                    });
        }
        return sb.toString();
    }

    private RestResponse error(RestCode restCode, Object data, HttpServletRequest request, Exception e) {
        final RestResponse rest = RestResponse.error(restCode, data);
        rest.setPath(request.getRequestURI());
        rest.setMsg(restCode.getMessage());
        if (e != null) {
            if(e instanceof BadSqlGrammarException){
                log.error(e.getMessage(), e);
                rest.setMsg(I18nUtil.getMessage("common.database.error"));
            }else{
                rest.setMsg(e.getMessage());
            }
            if (StringUtils.isBlank(rest.getMsg())) {
                rest.setMsg(ExceptionUtil.getSimpleMsg(e));
            }
            if (RestCode.INTERNAL_SERVER_ERROR == restCode && ( StrUtil.isBlank(rest.getMsg())|| StrUtil.contains(rest.getMsg(), "内部"))) {
                rest.setTrace(ExceptionUtil.getTraceMsg(e));
            }
            if (StringUtils.isBlank(rest.getMsg())) {
                rest.setMsg(I18nUtil.getMessage("common.internal.error"));
            }
        }
        rest.setRequestId(IdUtil.snowIdString());
        final String msg = StrUtil.format("[{}]:{}:{}[{}]", rest.getRequestId(),
                rest.getPath(), rest.getMsg(), rest.getTrace());
        if (RestCode.INTERNAL_SERVER_ERROR.getCode() == rest.getCode()) {
            log.error(msg);
        } else {
            log.warn(msg);
        }
        // 保存错误记录
        saveLog(rest);

        if (StrUtil.isNotBlank(rest.getMsg()) &&
                (rest.getMsg().contains("Cause: java.sql") || rest.getMsg().contains("SQLSyntaxError"))) {
            rest.setMsg(I18nUtil.getMessage("common.sql.error"));
            rest.setTrace(rest.getMsg());
        }
        return rest;
    }

    private void saveLog(final RestResponse ret) {
        if (ret == null || !logService.isPresent() || StrUtil.isBlank(ret.getRequestId())) {
            return;
        }
        final SysRequestErrorLog log = new SysRequestErrorLog();
        log.setTraceId(ret.getRequestId());
        log.setPath(ret.getPath());
        log.setUserId(RequestUtil.getUserId());
        log.setMsg(StrUtils.cut(ret.getMsg(), 500));
        log.setTrace(StrUtils.cut(ret.getTrace(), 1000));
        log.setCreateTime(DateTimeUtil.now());
        logService.get().save(log);
    }
}