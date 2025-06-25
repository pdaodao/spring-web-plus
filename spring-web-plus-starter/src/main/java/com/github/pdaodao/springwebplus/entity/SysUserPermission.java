package com.github.pdaodao.springwebplus.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import com.github.pdaodao.springwebplus.base.pojo.handler.MemberTypeListTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@TableName(value = "sys_user_permission", autoResultMap = true)
@Schema(description = "系统用户权限表")
public class SysUserPermission extends SnowIdWithTimeUserEntity {
    @TableFieldIndex
    @Schema(description = "用户id")
    private String userId;

    @TableFieldIndex
    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "团队名称")
    private transient String teamTitle;

    @TableFieldIndex
    @Schema(description = "命名空间")
    private String namespace;

    @TableFieldIndex
    @Schema(description = "对象id")
    private Long objId;

    @Schema(description = "授权操作")
    @TableField(typeHandler = MemberTypeListTypeHandler.class)
    private List<MemberType> memberTypes;
}
