package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@TableName(value = "sys_log", autoResultMap = true)
@Schema(description = "系统日志")
public class SysLog extends SnowIdWithTimeUserEntity {
    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "用户显示名称")
    private String userNickname;

    @Schema(description = "操作时间")
    private Date operationTime;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "模块")
    @Length(max = 200, message = "模块长度超过200限制")
    private String module;

    @Schema(description = "操作")
    @Length(max = 200, message = "操作长度超过200限制")
    private String description;

    @Schema(description = "ip地址")
    @Length(max = 100, message = "ip地址长度超过200限制")
    private String ip;

    @Schema(description = "ip地址描述")
    @Length(max = 200, message = "地址描述长度超过200限制")
    private String ipInfo;

    @Schema(description = "请求路径")
    @Length(max = 100, message = "请求路径长度超过100限制")
    private String path;

    @Schema(description = "请求参数")
    @Length(max = 500, message = "请求参数长度超过500限制")
    @TableFieldSize(500)
    private String params;

    @Schema(description = "是否成功")
    @TableFieldSize(value = 1, defaultValue = "0")
    private Boolean success;

    @Schema(description = "耗时，单位：毫秒")
    private Integer cost;

    @Schema(description = "请求来源地址")
    @Length(max = 500, message = "请求来源地址长度超过500限制")
    private String referer;

    public void setParams(String params) {
        this.params = StrUtils.cut(params, 400);
    }
}
