package com.github.pdaodao.springwebplus.base.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录信息")
public class LoginUserInfo {
    @Schema(description = "用户名")
    @NotBlank(message = "{validation.username_empty}")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "{validation.password_empty}")
    private String password;
}
