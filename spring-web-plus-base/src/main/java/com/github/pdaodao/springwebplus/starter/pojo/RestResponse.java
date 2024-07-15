package com.github.pdaodao.springwebplus.starter.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "接口返回数据封装")
public class RestResponse<T> implements IResponse {
    @Schema(description = "状态码")
    private Integer code;

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

    public RestResponse() {
    }

    public RestResponse(RestCode restCode, T data) {
        this.code = restCode.getCode();
        this.msg = restCode.getMessage();
        this.data = data;
    }


    public static RestResponse success(Object data) {
        return new RestResponse(RestCode.SUCCESS, data);
    }

    public static RestResponse error(RestCode restCode) {
        return new RestResponse(restCode, null);
    }

    public static RestResponse error(RestCode restCode, Object data) {
        return new RestResponse(restCode, data);
    }

    public static RestResponse error(RestCode restCode, String error) {
        RestResponse resp = new RestResponse(restCode, null);
        resp.setTrace(error);
        return resp;
    }
}

