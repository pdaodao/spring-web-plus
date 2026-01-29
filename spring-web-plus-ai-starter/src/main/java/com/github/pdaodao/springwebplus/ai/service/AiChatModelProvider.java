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
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
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
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        AiChatModel model = null;
        if(StrUtil.isNotBlank(modelId)){
            model = dao.detail(modelId);
        }
        if(model == null){
            final List<AiChatModel> list = dao.list(ChatModelType.LLM, teamId, true);
            if(CollUtil.isEmpty(list)){
                return SpringUtil.getBean(ChatModel.class);
            }
            model = list.get(RandomUtil.randomInt(list.size()));
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
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        AiChatModel model = null;
        if(StrUtil.isNotBlank(modelId)){
             model = dao.detail(modelId);
            Preconditions.checkNotNull(ChatModelType.EMBEDDING == model.getType(), "模型类型不是向量化模型");
        }
        if(model == null){
            final List<AiChatModel> list = dao.list(ChatModelType.EMBEDDING, teamId, true);
            if(CollUtil.isEmpty(list)){
                return SpringUtil.getBean(AiEmbedding.class);
            }
            model = list.get(RandomUtil.randomInt(list.size()));
        }
        return AiChatModelUtil.ofEmbedding(null, model.toOption());
    }
}
