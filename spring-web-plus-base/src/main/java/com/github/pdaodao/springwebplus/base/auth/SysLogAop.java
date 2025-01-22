package com.github.pdaodao.springwebplus.base.auth;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.log.Log;
import com.github.pdaodao.springwebplus.base.config.LogAopProperties;
import com.github.pdaodao.springwebplus.base.entity.Entity;
import com.github.pdaodao.springwebplus.base.ext.IpRegionUtil;
import com.github.pdaodao.springwebplus.base.ext.pojo.IpRegion;
import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.LogType;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.pojo.SysLog;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.base.util.IpUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Slf4j
@Aspect
@Order(1)
@Component
@EnableConfigurationProperties(LogAopProperties.class)
@ConditionalOnProperty(name = "log-aop.enable", havingValue = "true", matchIfMissing = true)

public class SysLogAop {
    public static ThreadLocal<SysLog> LOG = new ThreadLocal<>();
    private static Map<String, LogType> methodLogType = new ConcurrentHashMap<>();
    private static Map<String, String> classTagMap = new ConcurrentHashMap<>();
    @Autowired
    private LogAopProperties logAopProperties;

    @Autowired(required = false)
    private SysLogListener sysLogListener;

    @Around("@annotation(operation)")
    public Object apiLogAround(final ProceedingJoinPoint point, final Operation operation) throws Throwable {
        final HttpServletRequest request = RequestUtil.getRequest();
        final String servletPath = request.getServletPath();
        final boolean isHandleLog = isHandleLog(servletPath);
        if (!isHandleLog) {
            return point.proceed();
        }
        SysLog sysLog = null;
        try {
            sysLog = handleBefore(point, request, servletPath, operation);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        Object result = null;
        try {
            result = point.proceed();
            // 执行方法之后处理
            handleAfter(result, sysLog);
        } catch (Throwable e) {
            sysLog.setSuccess(false);
            handleException(e, sysLog);
            throw e;
        } finally {
            LOG.remove();
            handleFinally(sysLog);
        }
        return result;
    }


    /**
     * 执行方法之前
     *
     * @param joinPoint
     * @param request
     * @param servletPath
     * @throws Exception
     */
    private SysLog handleBefore(final ProceedingJoinPoint joinPoint, final HttpServletRequest request, final String servletPath, final Operation operation) throws Exception {
        // 系统日志
        final SysLog sysLog = new SysLog();
        sysLog.setSuccess(true);
        // 将日志保存到当前线程中
        LOG.set(sysLog);
        try {
            final Signature signature = joinPoint.getSignature();
            final MethodSignature methodSignature = (MethodSignature) signature;
            final Method method = methodSignature.getMethod();
            sysLog.setPath(servletPath);
            sysLog.setOperationTime(new Date());
            // 设置日志链路ID
            String traceId = MDC.get("traceId");
            sysLog.setTraceId(traceId);

            handleIpArea(sysLog);

            // 处理方法上的注解
            handleAnnotation(joinPoint, sysLog, operation);
            // 处理请求参数
            final String params = handleRequestParam(joinPoint, method, sysLog);
            sysLog.setParams(StrUtils.cut(params, 300));

            // 来源地址
            final String referer = request.getHeader("Referer");
            sysLog.setReferer(referer);

            // 处理用户代理信息
            final String userAgentString = handleUserAgent(request, sysLog);
            // 用户来源
            final String origin = request.getHeader("Origion");
            if (StringUtils.isBlank(origin)) {
                if (StringUtils.isNotBlank(referer) && referer.contains("swagger_ui")) {
                    sysLog.setOrigin("SWAGGER_UI");
                }
            } else {
                sysLog.setOrigin(origin);
            }
            if (StringUtils.isNotBlank(userAgentString)) {
                if (userAgentString.toLowerCase().contains("postman")) {
                    sysLog.setOrigin("postman");
                }
            }
            String requestOrigion = request.getHeader("Request-Origion");
            if ("Knife4j".equals(requestOrigion)) {
                sysLog.setOrigin("Knife4j");
            }
            // 处理登录人信息
            handleLoginUser(sysLog);
            StringBuffer requestURL = request.getRequestURL();
            printLog("requestURL: " + requestURL);
            // 打印请求日志
            printLog(sysLog);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return sysLog;
    }

    /**
     * 执行方法之后
     *
     * @param result
     */
    private void handleAfter(final Object result, final SysLog sysLog) {
        try {
            handleLoginUser(sysLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 执行方法异常
     *
     * @param e
     */
    private void handleException(final Throwable e, final SysLog sysLog) {
        try {
            sysLog.setSuccess(false);
            sysLog.setMsg(StrUtils.cut(ExceptionUtil.getSimpleMsg(e), 50));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 执行方法结束后处理
     */
    private void handleFinally(final SysLog sysLog) {
        if(sysLogListener != null){
            try{
                if(sysLog.getOperationTime() != null){
                    sysLog.setCost((int)(System.currentTimeMillis() - sysLog.getOperationTime().getTime()));
                }
                sysLogListener.onSave(sysLog);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 打印日志
     *
     * @param sysLog
     */
    private void printLog(SysLog sysLog) {
        if (logAopProperties.isPrint()) {
            if (sysLog != null) {
                String requestLog = "request: " +sysLog.getPath()  + "," + sysLog.getParams();
                printLog(requestLog);
            }
        }
    }

    /**
     * 打印日志
     *
     * @param msg
     */
    private void printLog(String msg) {
        if (logAopProperties.isPrint()) {
            log.info(msg);
        }
    }

    private void handleLoginUser(SysLog sysLog){
        if(sysLog.getUserId() != null && sysLog.getUserId() > 0){
            return;
        }
        sysLog.setUserId(RequestUtil.getUserId());
        sysLog.setUsername(RequestUtil.getUserNickname());
    }

    /**
     * 处理方法注解
     *
     * @param point
     * @param sysLog
     */
    private void handleAnnotation(final ProceedingJoinPoint point, final SysLog sysLog, final Operation operation) {
        sysLog.setOperation(operation.summary());
        final String className = point.getTarget().getClass().getSimpleName();
        // 获取模块
        final String moduleName = getModuleName(className, point);
        sysLog.setModule(moduleName);
        // 操作类型
        final LogType logType = parseLogType(className + ":" + moduleName + ":" + getMethodName(point), point, sysLog.getPath());
        sysLog.setLogType(logType);
    }

    private String handleRequestParam(final ProceedingJoinPoint point, final Method method, final SysLog sysLog) {
        final Annotation[][] annotations = method.getParameterAnnotations();
        final boolean isBody = isRequestBody(annotations);
        if (isBody) {
            // requestBodyString = (String) request.getAttribute(CommonConstant.REQUEST_PARAM_BODY_STRING);
        }
        final Map<String, Object> params = new HashMap<>();
        final String[] argNames = ((MethodSignature) point.getSignature()).getParameterNames();
        final Object[] argValues = point.getArgs();
        if (argValues != null && argNames != null && argValues.length == argNames.length) {
            int index = -1;
            for (final Object arg : argValues) {
                index++;
                if (arg == null) {
                    continue;
                }
                if (arg instanceof ServletRequest || arg instanceof ServletResponse) {
                    continue;
                }
                if (arg instanceof CurrentUserInfo) {
                    continue;
                }
                if (arg instanceof PageRequestParam) {
                    sysLog.setLogType(LogType.QUERY);
                    BeanUtils.copyPropertiesIgnoreNull(arg, params, "pwd", "password");
                    continue;
                }
                if (arg instanceof MultipartFile) {
                    if (StrUtil.contains(sysLog.getPath(), "excel")) {
                        sysLog.setLogType(LogType.EXCELUPLOAD);
                    } else {
                        sysLog.setLogType(LogType.UPLOAD);
                    }
                    continue;
                }
                if (arg instanceof Entity<?>) {
                    final Object id = ((Entity) arg).getId();
                    if (isBody && LogType.DELETE != sysLog.getLogType()) {
                        if (id != null) {
                            sysLog.setLogType(LogType.UPDATE);
                        } else {
                            sysLog.setLogType(LogType.CREATE);
                        }
                    }
                    BeanUtils.copyPropertiesIgnoreNull(arg, params, "pwd", "password");
                    continue;
                }
                if(BeanUtil.isBean(arg.getClass())){
                    BeanUtils.copyPropertiesIgnoreNull(arg, params, "pwd", "password");
                    continue;
                }
                params.put(argNames[index], arg);
            }
        }
        return JsonUtil.toJsonString(params);
    }


    /**
     * 处理用户代理环境
     *
     * @param request
     * @param sysLog
     * @return
     */
    private String handleUserAgent(HttpServletRequest request, SysLog sysLog) {
        // 用户环境
        String userAgentString = request.getHeader("USER_AGENT");
        sysLog.setUserAgent(userAgentString);
        UserAgent userAgent;
        try {
            userAgent = UserAgentUtil.parse(userAgentString);
            if (userAgent != null) {
                // 是否是手机
                boolean isMobile = userAgent.isMobile();
//                sysLog.setIsMobile(isMobile);
//                // 操作系统平台名称
//                Platform platform = userAgent.getPlatform();
//                String platformName = platform.getName();
//                sysLog.setPlatformName(platformName);
//                // 浏览器名称
//                Browser browser = userAgent.getBrowser();
//                String browserName = browser.getName();
//                sysLog.setBrowserName(browserName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userAgentString;
    }

    /**
     * 是否是json参数请求
     *
     * @param annotationArrays
     * @return
     */
    private boolean isRequestBody(Annotation[][] annotationArrays) {
        if (annotationArrays == null) {
            return false;
        }
        for (Annotation[] annotationArray : annotationArrays) {
            if (ArrayUtils.isNotEmpty(annotationArray)) {
                for (Annotation annotation : annotationArray) {
                    if (annotation instanceof RequestBody) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 日志类型
     */
    private LogType parseLogType(final String key, final ProceedingJoinPoint point, final String url) {
        LogType logType = methodLogType.get(key);
        if (logType != null) {
            return logType;
        }
        final String lowerCase = url.toLowerCase();
        // 通过url解析
        if (lowerCase.contains("delete") || lowerCase.contains("remove")) {
            logType = LogType.DELETE;
        } else if (lowerCase.contains("save")) {
            logType = LogType.CREATE;
        } else if (lowerCase.contains("update")) {
            logType = LogType.UPDATE;
        } else if (lowerCase.contains("create")) {
            logType = LogType.CREATE;
        } else if (lowerCase.contains("export")) {
            logType = LogType.EXCELDOWN;
        }else if(lowerCase.contains("detail") || lowerCase.contains("info") || lowerCase.contains("getById")){
            logType = LogType.DETAIL;
        }
        if (logType != null) {
            methodLogType.put(key, logType);
        }
        return logType;
    }

    private String getModuleName(final String className, final ProceedingJoinPoint point) {
        String classTag = classTagMap.get(className);
        if (classTag == null) {
            String v = "";
            final Tag tag = point.getTarget().getClass().getAnnotation(Tag.class);
            if (tag != null) {
                v = tag.name();
            }
            classTag = v;
            classTagMap.put(className, v);
        }
        return classTag;
    }

    /**
     * 方法名
     */
    private String getMethodName(final ProceedingJoinPoint point) {
        return point.getSignature().getName();
    }


    /**
     * 处理IP区域
     *
     * @param sysLog
     */
    private void handleIpArea(final SysLog sysLog) {
        final String ip = IpUtil.getRequestIp();
        sysLog.setIp(ip);
        try {
            // 设置IP归属地信息
            final IpRegion ipRegion = IpRegionUtil.getIpRegion(ip);
            if (ipRegion != null) {
                final StringBuilder sb = new StringBuilder();
                if(StrUtil.isNotBlank(ipRegion.getAreaDesc())){
                    sb.append(ipRegion.getAreaDesc());
                }
                if(StrUtil.isNotBlank(ipRegion.getIsp())){
                    sb.append("-").append(ipRegion.getIsp());
                }
                sysLog.setIpInfo(sb.toString());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 是否处理日志
     *
     * @param servletPath
     * @return
     */
    private boolean isHandleLog(final String servletPath) {
        final List<String> excludePaths = logAopProperties.excludePathList();
        if (CollUtil.isEmpty(excludePaths)) {
            return true;
        }
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (String excludePath : excludePaths) {
            final boolean match = antPathMatcher.match(excludePath, servletPath);
            if (match) {
                return false;
            }
        }
        return true;
    }
}





