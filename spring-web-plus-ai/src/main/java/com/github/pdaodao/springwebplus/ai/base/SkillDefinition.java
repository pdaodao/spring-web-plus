package com.github.pdaodao.springwebplus.ai.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "skill")
public class SkillDefinition {
    @Schema(description = "技能唯一标识（小写、横杠分隔），对应 /name 命令")
    private String name;

    @Schema(description = "最关键！要写清楚「Use when ...」，Claude 靠这个自动匹配是否触发")
    private String description;

    @Schema(description = "技能的完整实现内容")
    private String content;

    @Schema(description = "技能来源：mcp、user、builtin")
    private String source;

    @Schema(description = "技能实现文件路径")
    private String path;

    @Schema(description = "技能基准目录")
    private String baseDir;

    @Schema(description = "CLI 命令名称，用于 /command-name 触发")
    private String commandName;

    @Schema(description = "可读的显示名称")
    private String displayName;

    @Schema(description = "别名列表，可通过多个名称触发")
    private List<String> aliases;

    @Schema(description = "是否可手动 / 调用；false 则只能自动触发")
    private Boolean userInvocable = true;

    @Schema(description = "是否禁止模型自动调用此技能")
    private Boolean disableModelInvocation;

    @Schema(description = "指定使用的模型，不指定则使用默认模型")
    private String model;

    @Schema(description = "参数提示文字")
    private String argumentHint;

    @Schema(description = "限制技能可用工具，防止越权")
    private List<String> allowedTools;
}