package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName(value = "sys_notice", autoResultMap = true)
@Schema(description = "系统公告")
public class SysNotice extends BaseEntity {
    private String title;

    private String remark;

    private String color;
}
