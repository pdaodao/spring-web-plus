package com.github.pdaodao.springwebplus.starter.support;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.starter.entity.SnowIdEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "sys_access_error_log", autoResultMap = true)
public class SysRequestErrorLog extends SnowIdEntity {
    // 用户id
    private String userId;

    private String userName;

    // 请求路径
    private String path;

    // 错误信息
    private String msg;

    private String trace;

    private Date createTime;
}
