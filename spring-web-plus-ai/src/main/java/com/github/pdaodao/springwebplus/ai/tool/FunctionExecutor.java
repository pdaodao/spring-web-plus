package com.github.pdaodao.springwebplus.ai.tool;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.frame.ChatContext;
import com.github.pdaodao.springwebplus.ai.support.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class FunctionExecutor {

    public List<Tool> tools(){
        final Tool tableNames = Tool.ofFunction("tableNames", "数据表列表仅包含表名不含字段信息")
                .addInputParam("keyword", "关键词", false);
        return ListUtil.list(false, tableNames);
    }

    /**
     * 调用工具链函数
     * @param message
     * @param functionContext
     * @return
     */
    public List<ChatMessage> invoke(final ChatMessage message, ChatContext functionContext) throws Exception{
        if(message == null || !message.responseIsTool()){
            return null;
        }
        final List<ChatMessage> list = new ArrayList<>();
        for(final ToolCall toolCall : message.getToolCalls()){
            if(toolCall.getFunction() == null){
                continue;
            }
            final String fnName = toolCall.getFunction().getName();
            final String params = toolCall.getFunction().getArguments();
            log.info("-- ai-agent execute fn: ", fnName, toolCall.printMsg());
            final String content = invokeFn(functionContext, fnName,  params);
            if(StrUtil.isBlank(content)){
                continue;
            }
            list.add(ChatMessage.ofTool(toolCall.getId(), fnName, content));
        }
        return list;
    }

    private String invokeFn(final ChatContext functionContext, final String fnName, final String args) throws Exception{
        if("tableNames".equals(fnName)){
            return tableNames(functionContext, args);
        }
        if("tableInfo".equals(fnName)){
           return null;
        }
        return StrUtil.format("函数{}不存在.",fnName);
    }


    /**
     * 查询数据表列表
     * @param context
     * @param params
     * @return
     * @throws Exception
     */
    private String tableNames(final ChatContext context, String params) throws Exception{
        return null;
    }
}
