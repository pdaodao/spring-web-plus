package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.pojo.ChatAppConfig;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeUserEntity;
import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.frame.StringListJsonHandler;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.base.pojo.handler.IdTitleListTypeHandler;
import com.github.pdaodao.springwebplus.tool.data.IdTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "问答场景")
@TableName(value = "ai_chat_app", autoResultMap = true)
public class AiChatApp extends SnowIdWithTimeUserEntity implements WithTeam {
    @Schema(description = "标题")
    private String title;

    @Schema(description = "问答类型")
    private AiChatType chatType;

    @Schema(description = "模型id可选")
    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private String modelId;

    @Schema(description = "模型名称")
    private transient String modelTitle;

    @Schema(description = "开场白")
    @Size(max = 256, message = "开场白最大不能超过256")
    private String welcome;

    @Schema(description = "示例问题")
    @TableFieldSize(3000)
    @TableField(typeHandler = StringListJsonHandler.class)
    private List<String> examples;

    @Schema(description = "提示词")
    private String prompt;

    @Schema(description = "知识库列表")
    @TableField(typeHandler = IdTitleListTypeHandler.class)
    private List<IdTitle> knowledgeIds;

    @Schema(description = "是否启用术语")
    @TableFieldSize(defaultValue = "true")
    private Boolean termEnabled;

    @Schema(description = "数据源")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private IdTitle dbInfo;

    @Schema(description = "数据表列表")
    @TableField(typeHandler = IdTitleListTypeHandler.class)
    private List<IdTitle> tableList;

    @Schema(description = "场景配置")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ChatAppConfig appConfig;

    @TableFieldIndex
    @Schema(description = "团队-租户id")
    private String teamId;

    @Schema(description = "图标文件路径")
    @Size(max = 256, message = "图标长度最大不能超过256")
    private String icon;

    @Schema(description = "顺序")
    private Integer seq;
}