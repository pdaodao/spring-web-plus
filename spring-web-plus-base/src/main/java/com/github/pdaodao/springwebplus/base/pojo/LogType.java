package com.github.pdaodao.springwebplus.base.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "操作类型")
public enum LogType {
    @Schema(description = "登录")
    LOGIN,
    @Schema(description = "退出")
    LOGOUT,
    @Schema(description = "创建")
    CREATE,
    @Schema(description = "创建")
    UPDATE,
    @Schema(description = "删除")
    DELETE,
    @Schema(description = "查询")
    QUERY,
    @Schema(description = "详情")
    DETAIL,
    @Schema(description = "上传文件")
    UPLOAD,
    @Schema(description = "文件下载")
    DOWNLOAD,
    @Schema(description = "excel文件上传")
    EXCELUPLOAD,
    @Schema(description = "excel文件下载")
    EXCELDOWN,
}
