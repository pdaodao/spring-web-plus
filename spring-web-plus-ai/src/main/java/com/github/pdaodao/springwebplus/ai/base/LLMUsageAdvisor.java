//package com.github.pdaodao.springwebplus.ai.base;
//
//import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
//import org.springframework.ai.chat.client.ChatClientRequest;
//import org.springframework.ai.chat.client.ChatClientResponse;
//import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
//import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
//import org.springframework.ai.chat.metadata.Usage;
//
//public class LLMUsageAdvisor implements BaseAdvisor {
//    private LLMUsage llmUsage = null;
//    private Long start = null;
//    private Long end = null;
//
//    public static LLMUsageAdvisor of(){
//        return new LLMUsageAdvisor();
//    }
//
//    @Override
//    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
//        if(start == null){
//            start = DateTimeUtil.currentTimeMillis();
//        }
//        return chatClientRequest;
//    }
//
//    @Override
//    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
//        if(end == null){
//            end = DateTimeUtil.currentTimeMillis();
//        }
//        final Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();
//        llmUsage = LLMUsage.of(usage);
//        return chatClientResponse;
//    }
//
//    /**
//     * 用量
//     * @return
//     */
//    public LLMUsage getLlmUsage() {
//        return llmUsage;
//    }
//
//    /**
//     * 耗时毫秒
//     * @return
//     */
//    public Integer getCost(){
//        if(end == null || start == null){
//            return null;
//        }
//        return (int)(end - start);
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
