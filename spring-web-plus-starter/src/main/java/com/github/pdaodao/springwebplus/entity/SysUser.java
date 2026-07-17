package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName(value = "sys_user", autoResultMap = true)
@Schema(description = "系统用户")
public class SysUser extends SnowIdWithTimeUserEntity {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    @Length(max = 32, message = "用户名长度超过限制")
    private String username;

    @Schema(description = "昵称")
    @Length(max = 32, message = "昵称长度超过32限制")
    private String nickname;

    @Schema(description = "密码")
    @Length(max = 64, message = "密码长度超过限制")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String password;

    @Schema(description = "盐值")
    @Length(max = 16, message = "长度超过限制")
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String salt;

    @Schema(description = "手机号码")
    @Length(max = 32, message = "手机号长度超过限制")
    private String phone;

    @Schema(description = "电子邮件")
    @Length(max = 64, message = "邮件长度超过限制")
    private String email;

    @Schema(description = "头像")
    @Length(max = 300, message = "头像长度超过限制")
    private String avatar;

    @Schema(description = "城市名称如杭州")
    private String city;

    @Schema(description = "微信的用户id")
    private String wxid;

    @Schema(description = "支付宝用户id")
    private String zfbid;

    @Schema(description = "密码更新时间")
    private LocalDateTime pwdUpdateTime;

    @Schema(description = "上次登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "最后登录id")
    private String loginIp;

    @Schema(description = "状态，0：禁用，1：启用")
    @TableFieldSize(defaultValue = "1")
    private Boolean enabled;

    @Schema(description = "语言")
    @TableFieldSize(defaultValue = "zh-cn")
    private String locale;

    @Schema(description = "角色列表")
    private transient List<SysRole> roleList;
}
