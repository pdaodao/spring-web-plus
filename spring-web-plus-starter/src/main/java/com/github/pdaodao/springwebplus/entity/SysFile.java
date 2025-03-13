package com.github.pdaodao.springwebplus.entity;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

/**
 * 系统文件
 */
@Data
@TableName(value = "sys_file", autoResultMap = true)
@Schema(description = "系统文件")
public class SysFile extends SnowIdWithTimeUserEntity {
    @Schema(description = "文件名称")
    @Length(max = 500, message = "文件名称长度超过500限制")
    private String name;

    @Schema(description = "命名空间")
    private String namespace;

    @Schema(description = "对象id")
    private String objId;

    @Schema(description = "存储路径")
    @Length(max = 800, message = "存储路径长度超过800限制")
    private String path;

    @Schema(description = "类型")
    @Length(max = 100, message = "类型长度超过100限制")
    private String contentType;

    @Schema(description = "大小")
    private Long size;

    @Schema(description = "可读大小")
    private transient String readableSize;

    public String getReadableSize() {
        if (Objects.isNull(size)) {
            return "0";
        }
        return FileUtil.readableFileSize(size);
    }
}

