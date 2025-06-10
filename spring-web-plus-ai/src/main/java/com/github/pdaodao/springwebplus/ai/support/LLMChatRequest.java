package com.github.pdaodao.springwebplus.ai.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pdaodao.springwebplus.ai.tool.Tool;
import com.github.pdaodao.springwebplus.ai.tool.ToolCall;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LLMChatRequest implements Cloneable{
    private String model;
    // 使用什么采样温度，介于 0 和 1 之间。较高的值（如 0.7）将使输出更加随机，而较低的值（如 0.2）将使其更加集中和确定性
    private double temperature = 0.3f;

    private List<ChatMessage> messages;

    @JsonProperty("max_tokens")
    private Integer maxTokens = 4000;

    // 频率惩罚，介于-2.0到2.0之间的数字。正值会根据新生成的词汇在文本中现有的频率来进行惩罚，减少模型一字不差重复同样话语的可能性
    @JsonProperty("frequency_penalty")
    private Double frequencyPenalty;

    // 存在惩罚，介于-2.0到2.0之间的数字。正值会根据新生成的词汇是否出现在文本中来进行惩罚，增加模型讨论新话题的可能性
    @JsonProperty("presence_penalty")
    private Double presencePenalty;

    private Boolean stream = false;

    // 工具说明
    private List<Tool> tools;


    // private List<Function> functions;

    public static LLMChatRequest of(){
        return new LLMChatRequest();
    }

    public LLMChatRequest tool(final Tool tool){
        if(tools == null){
            tools = new ArrayList<>();
        }
        tools.add(tool);
        return this;
    }

    public LLMChatRequest tools(final List<Tool> tools){
        this.tools = tools;
        return this;
    }

    public LLMChatRequest model(final String model){
        this.model = model;
        return this;
    }

    public LLMChatRequest temperature(Double temperature){
        if(temperature == null || temperature > 1 || temperature <= 0){
            return this;
        }
        this.temperature = temperature;
        return this;
    }

    public LLMChatRequest addMsg(final ChatRole role, final String msg){
        if(StrUtil.isBlank(msg)){
            return this;
        }
        if(messages == null){
            messages = new ArrayList<>();
        }
        messages.add(ChatMessage.of(role, msg));
        return this;
    }

    public LLMChatRequest appendMsg(final ChatMessage msg){
        if(msg == null){
            return this;
        }
        if(messages == null){
            messages = new ArrayList<>();
        }
        messages.add(msg);
        return this;
    }

    public LLMChatRequest appendMsgs(final List<ChatMessage> msgs){
        if(CollUtil.isEmpty(msgs)){
            return this;
        }
        if(messages == null){
            messages = new ArrayList<>();
        }
        messages.addAll(msgs);
        return this;
    }

    @Override
    public LLMChatRequest clone(){
        final LLMChatRequest cc = new LLMChatRequest();
        BeanUtils.copyProperties(this, cc, "messages");
        cc.setMessages(ListUtil.list(false, this.getMessages()));
        return cc;
    }

    public LLMChatRequest setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        return this;
    }

    public String printMsg(final int index){
        final StringBuilder sb = new StringBuilder();
        sb.append("\n").append("「").append(index).append("」");
        sb.append("-- LLM-request -- ");
        for(final ChatMessage msg: getMessages()){
            sb.append("\n[").append(msg.getRole()).append("]");
            if(StrUtil.isNotBlank(msg.getToolCallId())){
                sb.append("(ToolCallId:").append(msg.getToolCallId()).append(")");
            }
            sb.append(msg.getContent());

            if(CollUtil.isNotEmpty(msg.getToolCalls())){
                for(final ToolCall toolCall : msg.getToolCalls()){
                    sb.append("\n  ").append(toolCall.getId()).append(":").append(toolCall.printMsg());
                }
            }
        }
        sb.append("\n").append("「").append(index).append("」");
        sb.append("-- LLM-request -- end \n");
        return sb.toString();
    }
}
