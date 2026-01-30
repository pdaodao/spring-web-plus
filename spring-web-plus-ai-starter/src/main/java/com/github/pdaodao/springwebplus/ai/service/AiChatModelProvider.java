package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiEmbedding;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.core.AiChatModelUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import org.springframework.ai.chat.model.ChatModel;
import java.util.List;

public class AiChatModelProvider {
    /**
     * 通过模型配置信息获取问答模型
     * @param teamId
     * @param modelId
     * @return
     */
    public static ChatModel of(final String teamId, final String modelId){
        final AiChatModel model = ofModelInfo(teamId, modelId, ChatModelType.LLM);
        if(model == null){
            return SpringUtil.getBean(ChatModel.class);
        }
        return AiChatModelUtil.of(model.getProviderId(), model.toOption());
    }

    /**
     * 通过页面模型配置信息获取向量化模型
     * @param teamId
     * @param modelId
     * @return
     */
    public static AiEmbedding ofEmbedding(final String teamId, final String modelId){
        final AiChatModel model = ofModelInfo(teamId, modelId, ChatModelType.EMBEDDING);
        if(model == null){
            return SpringUtil.getBean(AiEmbedding.class);
        }
        return AiChatModelUtil.ofEmbedding(null, model.toOption());
    }

    /**
     * 获取模型配置信息
     * @param teamId
     * @param modelId
     * @param modelType
     * @return
     */
    public static AiChatModel ofModelInfo(final String teamId, final String modelId, ChatModelType modelType){
        if(modelType == null){
            modelType = ChatModelType.LLM;
        }
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        if(StrUtil.isNotBlank(modelId)){
            final AiChatModel model = dao.detail(modelId);
            if(model != null && modelType == model.getType()){
                return model;
            }
        }
        final List<AiChatModel> list = dao.list(modelType, teamId, true);
        if(CollUtil.isEmpty(list)){
            return null;
        }
        return list.get(RandomUtil.randomInt(list.size()));
    }
}
