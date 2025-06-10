package com.github.pdaodao.springwebplus.ai.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.pdaodao.springwebplus.ai.tool.ToolCall;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LLMChatResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private Error error;

//    public static CompletionResponse parse(final String json){
//        if(StrUtil.isBlank(json)){
//            return null;
//        }
//        try{
//            return JsonUtil.objectMapper.readValue(json, CompletionResponse.class);
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
//    }

    public String firstMessageContent(){
        if(CollUtil.isEmpty(choices)){
            return null;
        }
        for(final Choice ch: choices){
            if(ch.getMessage() != null && StrUtil.isNotBlank(ch.getMessage().getContent())){
                return ch.getMessage().getContent();
            }
        }
        return choices.get(0).getMessage().getContent();
    }

    public ChatMessage firstMessage(){
        if(CollUtil.isEmpty(choices)){
            return null;
        }
        for(final Choice ch: choices){
            if(ch.getMessage() != null){
                return ch.getMessage();
            }
        }
        return choices.get(0).getMessage();
    }

    public List<ChatMessage> messages(){
        if(CollUtil.isEmpty(choices)){
            return null;
        }
        final List<ChatMessage> list = new ArrayList<>();
        for(final Choice ch: choices){
            if(ch.getMessage() != null){
                list.add(ch.getMessage());
            }
        }
        return list;
    }


    /**
     * 返回结果是否含有工具
     * @return
     */
    public boolean responseHasTool(){
        if(CollUtil.isEmpty(choices)){
            return false;
        }
        for(final Choice ch: choices){
            if(ch.getMessage() != null && ch.getMessage().responseIsTool()){
                return true;
            }
        }
        return false;
    }

    public boolean isStreamStop(){
        if(CollUtil.isEmpty(choices)){
            return true;
        }
        return StrUtil.isNotBlank(choices.get(0).getFinishReason());
    }

    public String firstDelta(){
        if(CollUtil.isEmpty(choices)){
            return null;
        }
        final ChatMessage msg = choices.get(0).getDelta();
        if(msg == null){
            return null;
        }
        final String ct = msg.getContent();
        if(ct == null){
            return StrUtil.EMPTY;
        }
        return ct;
    }

    public String printMsg(final int index, final long costMs){
        final StringBuilder sb = new StringBuilder();
        sb.append("\n").append("「").append(index).append("」");

        sb.append("-- LLM-response -- \n");
        for(final Choice choice: getChoices()){
            if(choice.getMessage() == null){
                continue;
            }
            sb.append("[").append(choice.getMessage().getRole()).append("]");
            sb.append(choice.getMessage().getContent());
            if(CollUtil.isNotEmpty(choice.getMessage().getToolCalls())){
                for(final ToolCall toolCall: choice.getMessage().getToolCalls()){
                    sb.append("\n");
                    sb.append(StrUtil.repeat("-", 10));
                    sb.append("[call:id-").append(toolCall.getId()).append("]");
                    sb.append(toolCall.getFunction().getName());
                    sb.append("(");
                    sb.append(StrUtil.isBlank(toolCall.getFunction().getArguments()) ? "" : toolCall.getFunction().getArguments().replaceAll("\n", " "));
                    sb.append(")");
                }
            }
        }
        sb.append("\n");
        sb.append("[usage]");
        sb.append(" prompt_tokens=").append(usage.getPromptTokens());
        sb.append(", completion_tokens=").append(usage.getCompletionTokens());
        sb.append(", total_tokens=").append(usage.getTotalTokens());
        sb.append("  cost time(ms):").append(costMs);
        sb.append("\n").append("「").append(index).append("」");
        sb.append("-- LLM-response -- end.\n");
        return sb.toString();
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice{
        private Integer index;
        // stream
        private ChatMessage delta;
        private ChatMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
    }


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error{
        private String message;
        private String type;
    }
}
