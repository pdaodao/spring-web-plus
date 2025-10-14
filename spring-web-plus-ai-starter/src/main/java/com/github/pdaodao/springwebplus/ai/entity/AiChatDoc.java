package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocNamespace;
import com.github.pdaodao.springwebplus.base.entity.*;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Schema(description = "知识库文档")
@TableName(value = "ai_chat_doc", autoResultMap = true)
public class AiChatDoc extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete, WithChildren<AiChatDoc> {
    @Schema(description = "标题-文件名称")
    private String title;

    @Schema(description = "英文名称 如数据表名")
    private String name;

    @Schema(description = "文档大类")
    private ChatDocNamespace docNamespace;

    @Schema(description = "数据源id")
    private String dbId;

    @Schema(description = "sql语句")
    @Size(max = 3000, message = "sql语句长度超长")
    private String  sqlText;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "存储路径")
    @Length(max = 800, message = "存储路径长度超过800限制")
    private String filePath;

    @Schema(description = "文本块数")
    private Integer itemCount;

    @Schema(description = "字符数")
    private Integer charCount;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "true")
    private Boolean enabled;

    @Schema(description = "是否嵌入完成")
    private Boolean isEmbed;

    @TableLogic
    private Boolean isDeleted;

    private transient List<AiChatDoc> children;
}