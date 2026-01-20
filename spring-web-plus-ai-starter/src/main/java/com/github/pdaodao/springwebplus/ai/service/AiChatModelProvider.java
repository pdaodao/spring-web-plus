package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.core.AiChatModelUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.ai.chat.model.ChatModel;

public class AiChatModelProvider {
    public static ChatModel of(final String modelId){
        Preconditions.checkNotBlank(modelId, "modelId is blank.");
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        final AiChatModel aiChatModel = dao.detail(modelId);
        Preconditions.checkNotNull(aiChatModel, "model is null for {}", modelId);
        return AiChatModelUtil.of(aiChatModel.getProviderId(), aiChatModel.toOption());
    }
}
