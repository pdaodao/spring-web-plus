package com.github.apengda.springbootplus.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 返回状态码
 */
@AllArgsConstructor
@ToString
@Getter
public enum RestCode {
    /**
     * 成功
     */
    SUCCESS(200, "成功"),

    /**
     * 页面或资源不存在
     */
    NOT_FOUND(404, "页面或资源不存在"),


    /**
     * 编辑期间数据变动 需要询问查看当前是什么样的 或者 强制保存
     */
    VersionChanged(409, "版本改动"),

    /**
     * 请求参数错误 自己处理错误信息信息
     */
    NeedTip(402, "数据错误"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     *
     */
    METHOD_NOT_ALLOWED(405, "禁止访问"),


    /**
     * 请求参数错误
     */
    NOT_ACCEPTABLE(406, "请求参数错误"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),


    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "内部服务器错误"),

    /**
     * 网关错误
     */
    BAD_GATEWAY(502, "网关错误"),


    /**
     * 无效 token
     */
    INVALID_TOKEN(601, "无效Token"),

    /**
     * 无用户信息 需要登录
     */
    NO_USER_INFO(603, "用户未登录或已失效，请重新登录"),

    /**
     * 无访问权限
     */
    ACCESS_DENIED(700, "无访问权限"),

    /**
     * 无数据权限
     */
    NO_DATA_PERMISSION(800, "无数据权限"),

    /**
     * 部分数据权限
     */
    PART_DATA_PERMISSION(802, "部分数据权限");

    public final Integer code;
    public final String message;

}
