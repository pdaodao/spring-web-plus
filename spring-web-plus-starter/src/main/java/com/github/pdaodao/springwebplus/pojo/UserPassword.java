package com.github.pdaodao.springwebplus.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPassword {
    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "旧密码")
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "密码-新密码")
    @NotBlank(message = "新密码不能为空")
    private String password;
}