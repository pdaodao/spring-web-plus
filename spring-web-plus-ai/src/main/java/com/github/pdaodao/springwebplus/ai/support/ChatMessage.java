package com.github.pdaodao.springwebplus.ai.support;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pdaodao.springwebplus.ai.tool.ToolCall;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage {
    private ChatRole role;
    private String content;

    @JsonProperty("tool_call_id")
    private String toolCallId;

    // 函数名称
    private String name;

    // 函数返回
    @JsonProperty("tool_calls")
    private List<ToolCall> toolCalls;

    public ChatMessage(ChatRole role, String content) {
        this.role = role;
        this.content = content;
    }

    public boolean responseIsTool(){
        return CollUtil.isNotEmpty(toolCalls);
    }

    public static ChatMessage of(final ChatRole role, final String msg){
        return new ChatMessage(role, msg);
    }

    public static ChatMessage ofTool(final String id, final String fnName, final String content){
        final ChatMessage msg = of(ChatRole.tool, content);
        msg.setToolCallId(id);
        msg.name = fnName;
        return msg;
    }
}
