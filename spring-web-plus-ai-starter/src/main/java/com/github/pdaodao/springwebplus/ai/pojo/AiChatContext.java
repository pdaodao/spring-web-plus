package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.ai.base.AiChatType;
import com.github.pdaodao.springwebplus.ai.base.LLMRequest;
import com.github.pdaodao.springwebplus.ai.base.LLMResponse;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSessionMsg;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AiChatContext {
    private LLMRequest req;

    private AiChatApp chatApp;

    private AiChatType chatType;

    @Schema(description = "场景的阶段如生成sql，查询数据, 归因分析，预测分析等")
    private String phase;

    private String modelId;

    private LLMResponse response;

    private AiChatSessionMsg sessionMsg;

    private MsgSender msgSender;

    private List<TableInfo> tableInfoList;

    public static AiChatContext of(final LLMRequest req, final MsgSender msgSender){
        final AiChatContext context = new AiChatContext();
        context.setReq(req);
        context.setMsgSender(msgSender);
        AiChatContextHolder.set(context);
        return context;
    }

    public static AiEmbedTextQuery ofQuery(){
        final AiEmbedTextQuery query = new AiEmbedTextQuery();
        final AiChatContext context = AiChatContext.fromHolder();
        if(context != null && context.getChatApp() != null && context.getChatApp().getAppConfig() != null){
            query.setTopK(context.getChatApp().getAppConfig().getTopK());
            query.setScore(context.getChatApp().getAppConfig().getScore());
        }else{
            query.setTopK(5);
            query.setScore(0.6);
        }
        return query;
    }

    public static AiChatContext fromHolder(){
        return AiChatContextHolder.get();
    }

    public static void clear(){
        AiChatContextHolder.clear();
    }
}