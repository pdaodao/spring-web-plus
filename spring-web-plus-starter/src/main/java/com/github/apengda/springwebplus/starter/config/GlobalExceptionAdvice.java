package com.github.apengda.springwebplus.starter.config;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.starter.pojo.R;
import com.github.apengda.springwebplus.starter.pojo.RestCode;
import com.github.apengda.springwebplus.starter.pojo.RestException;
import com.github.apengda.springwebplus.starter.service.SysRequestErrorLogService;
import com.github.apengda.springwebplus.starter.support.SysRequestErrorLog;
import com.github.apengda.springwebplus.starter.util.ExceptionUtil;
import com.github.apengda.springwebplus.starter.util.IdUtil;
import com.github.apengda.springwebplus.starter.util.RequestUtil;
import com.github.apengda.springwebplus.starter.util.StrUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Optional;


@RestControllerAdvice
@Slf4j
@ConditionalOnProperty(value = "global.exception", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
public class GlobalExceptionAdvice {
    private final Optional<SysRequestErrorLogService> logService;

    @ExceptionHandler(RestException.class)
    public R restCodeException(RestException e, HttpServletRequest request, HttpServletResponse response) {
        final RestCode restCode = e != null ? e.getCode() : RestCode.INTERNAL_SERVER_ERROR;
        if (restCode.getCode() >= 700) {
            // 局部内容
            response.setStatus(206);
        } else if (restCode.getCode() > 600) {
            // 未授权
            response.setStatus(401);
        } else {
            response.setStatus(restCode.getCode());
        }
        return error(restCode, e.getData(), request, e);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().iterator().next().getMessage();
        return error(RestCode.NOT_ACCEPTABLE, null, request, new Exception(message));
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {

        return error(RestCode.NOT_ACCEPTABLE, null, request, RestException.invalidParam(processBindingResult(e.getBindingResult())));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public R handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        return error(RestCode.INTERNAL_SERVER_ERROR, null, request, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(IllegalArgumentException.class)
    public R handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        return error(RestCode.INTERNAL_SERVER_ERROR, null, request, e);
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

    private R error(RestCode restCode, Object data, HttpServletRequest request, Exception e) {
        final R rest = R.error(restCode, data);
        rest.setPath(request.getRequestURI());
        rest.setMsg(restCode.getMessage());
        if (e != null) {
            rest.setMsg(e.getMessage());
            if (StringUtils.isBlank(rest.getMsg())) {
                rest.setMsg(ExceptionUtil.getSimpleMsg(e));
            }
            if (RestCode.INTERNAL_SERVER_ERROR == restCode) {
                rest.setTrace(ExceptionUtil.getTraceMsg(e));
            }
            if (StringUtils.isBlank(rest.getMsg())) {
                rest.setMsg("内部服务器错误");
            }
        }
        rest.setRequestId(IdUtil.snowId());
        final String msg = StrUtil.format("[{}]:{}:{}[{}]", rest.getRequestId(),
                rest.getPath(), rest.getMsg(), rest.getTrace());
        if (RestCode.INTERNAL_SERVER_ERROR.getCode() == rest.getStatus()) {
            log.error(msg);
        } else {
            log.warn(msg);
        }
        // 保存错误记录
        saveLog(rest);

        if (StrUtil.isNotBlank(rest.getMsg()) &&
                (rest.getMsg().contains("Cause: java.sql") || rest.getMsg().contains("SQLSyntaxError"))) {
            rest.setMsg("内部sql错误");
            rest.setTrace(rest.getMsg());
        }
        return rest;
    }

    private void saveLog(final R ret) {
        if (ret == null || !logService.isPresent() || StrUtil.isBlank(ret.getRequestId())) {
            return;
        }
        final SysRequestErrorLog log = new SysRequestErrorLog();
        log.setId(ret.getRequestId());
        log.setPath(ret.getPath());
        log.setUserId(RequestUtil.getUserId());
        log.setMsg(StrUtils.cut(ret.getMsg(), 500));
        log.setTrace(StrUtils.cut(ret.getTrace(), 1000));
        log.setCreateTime(new Date());
        logService.get().save(log);
    }

}

