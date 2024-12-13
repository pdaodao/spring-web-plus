package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_oauth2_client", autoResultMap = true)
@Schema(description = "oauth2-客户端")
public class SysOauth2Client extends SnowIdWithTimeUserEntity {
    private String clientId;

    private String clientSecret;

    private String scopes;
}
