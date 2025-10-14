package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatRating;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocItems;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.entity.WithSql;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
@Schema(description = "大模型问答会话消息")
@TableName(value = "ai_chat_session_msg", autoResultMap = true)
public class AiChatSessionMsg extends SnowIdWithTimeEntity implements WithPidString, WithSql {
    @TableFieldIndex
    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "父问题id")
    private String pid;

    @Schema(description = "内部厂商消息id")
    private String msgId;

    @Schema(description = "问题")
    @Size(max = 3000, message = "问题长度最大不能超过3000")
    private String question;

    @Schema(description = "普通问答回答结果")
    @Size(max = 5000, message = "答案长度最大不能超过3000")
    private String answer;

    @Schema(description = "数据源id")
    private String dbId;

    @Schema(description = "sql语句")
    @TableFieldSize(4000)
    private String sqlText;

    @Schema(description = "文档-数据表列表")
    @TableFieldSize(5000)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ChatDocItems docItems;

    @Schema(description = "数据库查询结果")
    @TableFieldSize(5000)
    @TableField(typeHandler = JacksonTypeHandler.class)
    private TableData tableData;

    @Schema(description = "评价")
    @TableFieldSize(defaultValue = "none")
    private AiChatRating chatRating;

    @Schema(description = "错误消息")
    @Size(max = 3000, message = "错误消息不能超过3000个字")
    private String errorMsg;

    @Schema(description = "重试次数")
    private Integer retryTimes;

    @Schema(description = "大模型耗时(ms)")
    private Integer llmCost;

    @Schema(description = "数据库查询耗时(ms)")
    private Integer dbCost;

    @Schema(description = "输入token数")
    private Long inputTokens;

    @Schema(description = "输出token数")
    private Long outputTokens;

    @Schema(description = "总token数")
    private Long totalTokens;
}