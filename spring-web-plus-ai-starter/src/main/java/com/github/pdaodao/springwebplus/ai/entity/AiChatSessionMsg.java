package com.github.pdaodao.springwebplus.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatRating;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.WithPidString;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.base.frame.TableFieldSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.Size;

@Data
@Schema(description = "大模型问答会话消息")
@TableName(value = "ai_chat_session_msg", autoResultMap = true)
public class AiChatSessionMsg extends SnowIdWithTimeEntity implements WithPidString{
    @TableFieldIndex
    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "问答场景id")
    private String appId;

    @Schema(description = "父问题id")
    @TableFieldSize(defaultValue = "0")
    private String pid;

    @Schema(description = "问题")
    @Size(max = 3000, message = "问题长度最大不能超过3000")
    private String question;

    @TableFieldSize(5000)
    @Schema(description = "普通问答回答结果")
    @TableField(typeHandler = JacksonTypeHandler.class)
    private LLMResponse answer;

    @Schema(description = "数据源id")
    private String dbId;

    @Schema(description = "评价")
    @TableFieldSize(defaultValue = "none")
    private AiChatRating chatRating;

    @Schema(description = "错误消息")
    @Size(max = 3000, message = "错误消息不能超过3000个字")
    private String errorMsg;

    @Schema(description = "重试次数")
    @TableFieldSize(defaultValue = "0")
    private Integer retryTimes;

    @Schema(description = "大模型耗时(ms)")
    @TableFieldSize(defaultValue = "0")
    private Integer llmCost;

    @Schema(description = "输入token数")
    @TableFieldSize(defaultValue = "0")
    private Integer inputTokens;

    @Schema(description = "输出token数")
    @TableFieldSize(defaultValue = "0")
    private Integer outputTokens;

    @Schema(description = "总token数")
    @TableFieldSize(defaultValue = "0")
    private Integer totalTokens;
}