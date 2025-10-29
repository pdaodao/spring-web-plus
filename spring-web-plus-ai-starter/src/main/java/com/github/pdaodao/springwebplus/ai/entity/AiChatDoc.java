package com.github.pdaodao.springwebplus.ai.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocItemType;
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
public class AiChatDoc extends SnowIdWithTimeUserEntity implements WithTeam, WithEnabled, WithDelete,
        WithPidString, WithSql, WithChildren<AiChatDoc> {
    @Schema(description = "标题-文件名称")
    private String title;

    @Schema(description = "英文名称 如数据表名")
    private String name;

    @Schema(description = "来源id 如数据表id")
    private String tableId;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "文档大类")
    private String namespace;

    @Schema(description = "数据源id")
    private String dbId;

    @Schema(description = "sql语句")
    @Size(max = 3000, message = "sql语句长度超长")
    private String sqlText;

    @Schema(description = "文本块-字段列表")
    @TableField(exist = false)
    private List<AiChatDocItem> docItems;

    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "存储路径")
    @Length(max = 800, message = "存储路径长度超过800限制")
    private String filePath;

    @Schema(description = "文本块数")
    private Long itemCount;

    @Schema(description = "字符数")
    private Long charCount;

    @Schema(description = "父分类id")
    private String pid;

    @Schema(description = "是否是分类")
    @TableFieldSize(defaultValue = "false")
    private Boolean isDir;

    @Schema(description = "是否启用")
    @TableFieldSize(defaultValue = "true")
    private Boolean enabled;

    @Schema(description = "是否嵌入完成")
    private Boolean isEmbed;

    @TableLogic
    private Boolean isDeleted;

    private transient List<AiChatDoc> children;

    public String content() {
        final StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(name)) {
            sb.append(name).append(":");
        }
        if (StrUtil.isNotBlank(title)) {
            sb.append(title);
        }
        if (StrUtil.isNotBlank(remark)) {
            sb.append(":").append(remark);
        }
        return sb.toString();
    }

    // 子项是否是字段
    public boolean itemIsField() {
        return StrUtil.equals(ChatDocNamespace.table, namespace)
                || StrUtil.equals(ChatDocNamespace.sql, namespace)
                || StrUtil.equals(ChatDocNamespace.excel, namespace);
    }

    // 子项类型
    public String itemType() {
        return itemIsField() ? ChatDocItemType.field : ChatDocItemType.text;
    }
}