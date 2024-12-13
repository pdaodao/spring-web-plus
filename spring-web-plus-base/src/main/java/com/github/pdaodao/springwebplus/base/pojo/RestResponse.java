package com.github.pdaodao.springwebplus.base.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.PageInfo;
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

    @Schema(description = "分页信息")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo pageInfo;

    @Schema(description = "提示信息")
    private String msg;

    @Schema(description = "错误信息详情")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

    public static RestResponse success(Object data, final PageInfo pageInfo) {
        final RestResponse r = new RestResponse(RestCode.SUCCESS, data);
        r.setPageInfo(pageInfo);
        return r;
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

