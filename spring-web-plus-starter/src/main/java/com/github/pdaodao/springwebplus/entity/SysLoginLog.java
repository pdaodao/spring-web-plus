package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;

@Data
@TableName(value = "sys_login_log", autoResultMap = true)
@Schema(description = "系统登录日志")
public class SysLoginLog extends BaseEntity {
    @TableFieldIndex
    private String userId;

    @Schema(description = "用户显示名称")
    private String userNickname;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作")
    @Length(max = 200, message = "操作长度超过200限制")
    private String description;

    @Schema(description = "ip地址")
    @Length(max = 100, message = "ip地址长度超过200限制")
    private String ip;

    @Schema(description = "ip地址描述")
    @Length(max = 200, message = "地址描述长度超过200限制")
    private String ipInfo;

    @Schema(description = "耗时，单位：毫秒")
    private Integer cost;

    @Schema(description = "是否成功")
    @TableFieldSize(value = 1, defaultValue = "0")
    private Boolean success;

    @Schema(description = "请求来源地址")
    @Length(max = 500, message = "请求来源地址长度超过500限制")
    private String referer;
}