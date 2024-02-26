package com.github.apengda.springwebplus.starter.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "接口返回数据封装")
public class R<T> {
    @Schema(description = "状态码")
    private Integer status;

    @Schema(description = "数据")
    @JsonInclude
    private T data;

    @Schema(description = "提示信息")
    private String msg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "错误信息详情")
    private String trace;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "请求id")
    private String requestId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "请求路径")
    private String path;

    public R() {
    }

    public R(RestCode restCode, T data) {
        this.status = restCode.getCode();
        this.msg = restCode.getMessage();
        this.data = data;
    }


    public static R success(Object data) {
        return new R(RestCode.SUCCESS, data);
    }

    public static R error(RestCode restCode) {
        return new R(restCode, null);
    }

    public static R error(RestCode restCode, Object data) {
        return new R(restCode, data);
    }

    public static R error(RestCode restCode, String error) {
        R resp = new R(restCode, null);
        resp.setTrace(error);
        return resp;
    }
}
