package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName(value = "sys_team", autoResultMap = true)
@Schema(description = "系统团队表")
public class SysTeam extends SnowIdWithTimeUserEntity implements WithDelete {
    @Schema(description = "团队名称")
    private String title;

    @Schema(description = "创建者")
    private transient String creatorNickname;

    @TableLogic
    private Boolean isDeleted;

    @Schema(description = "成员类型")
    private transient MemberType permissionOperate;
}
