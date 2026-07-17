package com.github.pdaodao.springwebplus.base.pojo;

import com.github.pdaodao.springwebplus.base.util.I18nUtil;
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
    SUCCESS(200, "restcode.success"),

    /**
     * 页面或资源不存在
     */
    NOT_FOUND(404, "restcode.not_found"),


    /**
     * 编辑期间数据变动 需要询问查看当前是什么样的 或者 强制保存
     */
    VersionChanged(409, "restcode.version_changed"),

    /**
     * 请求参数错误 自己处理错误信息信息
     */
    NeedTip(402, "restcode.need_tip"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "restcode.forbidden"),

    /**
     *
     */
    METHOD_NOT_ALLOWED(405, "restcode.method_not_allowed"),


    /**
     * 请求参数错误
     */
    NOT_ACCEPTABLE(406, "restcode.not_acceptable"),

    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "restcode.request_timeout"),


    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "restcode.internal_server_error"),

    /**
     * 网关错误
     */
    BAD_GATEWAY(502, "restcode.bad_gateway"),


    /**
     * 无效 token
     */
    INVALID_TOKEN(401, "restcode.invalid_token"),

    /**
     * 无用户信息 需要登录
     */
    NO_USER_INFO(401, "restcode.no_user_info"),

    /**
     * 无访问权限
     */
    ACCESS_DENIED(403, "restcode.access_denied"),

    /**
     * 无数据权限
     */
    NO_DATA_PERMISSION(403, "restcode.no_data_permission"),

    /**
     * 部分数据权限
     */
    PART_DATA_PERMISSION(403, "restcode.part_data_permission");

    public final Integer code;
    public final String messageCode;

    /**
     * 获取国际化消息
     */
    public String getMessage() {
        return I18nUtil.getMessage(messageCode);
    }

}
