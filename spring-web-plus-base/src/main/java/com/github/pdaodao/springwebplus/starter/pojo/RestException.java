package com.github.pdaodao.springwebplus.starter.pojo;

import lombok.Getter;
import lombok.Setter;

public class RestException extends RuntimeException {
    @Setter
    @Getter
    protected RestCode code = RestCode.INTERNAL_SERVER_ERROR;

    private Object data;

    public RestException() {
    }

    public RestException(RestCode status, String message) {
        super(message);
        this.code = status;
    }

    public RestException(RestCode status, String message, Throwable throwable) {
        super(message, throwable);
        this.code = status;
    }

    public RestException(RestCode status, Throwable cause) {
        super(cause);
        this.code = status;
    }

    public RestException(RestCode status, String message, Object data) {
        super(message);
        this.code = status;
        this.data = data;
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public static RestException userNotFound(String msg) {
        return new RestException(RestCode.NO_USER_INFO, msg);
    }

    public static RestException notFound404(String msg) {
        return new RestException(RestCode.NOT_FOUND, msg);
    }

    public static RestException error500(String msg) {
        return new RestException(RestCode.INTERNAL_SERVER_ERROR, msg);
    }

    public static RestException error500(String msg, Throwable throwable) {
        if (throwable != null && throwable instanceof RestException) {
            return (RestException) throwable;
        }
        return new RestException(RestCode.INTERNAL_SERVER_ERROR, msg, throwable);
    }

    public static RestException error500(Throwable cause) {
        if (cause != null && cause instanceof RestException) {
            return (RestException) cause;
        }
        return new RestException(RestCode.INTERNAL_SERVER_ERROR, cause);
    }

    public static RestException invalidParam(String msg) {
        return new RestException(RestCode.NOT_ACCEPTABLE, msg);
    }

    public static RestException invalidParam(String msg, Throwable throwable) {
        if (throwable != null && throwable instanceof RestException) {
            return (RestException) throwable;
        }
        return new RestException(RestCode.NOT_ACCEPTABLE, msg, throwable);
    }

    public static RestException tips(String msg) {
        return new RestException(RestCode.NeedTip, msg);
    }

    public static RestException versionChanged(String msg) {
        return new RestException(RestCode.VersionChanged, msg);
    }

    public Object getData() {
        return data;
    }

    public RestException setData(Object data) {
        this.data = data;
        return this;
    }
}
