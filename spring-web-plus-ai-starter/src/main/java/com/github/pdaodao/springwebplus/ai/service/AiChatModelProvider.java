package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.core.AiChatModelFactory;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.DefaultChatOptions;

public class AiChatModelProvider {
    public static ChatModel of(final String modelId, DefaultChatOptions chatOptions){
        Preconditions.checkNotBlank(modelId, "modelId is blank.");
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        final AiChatModel aiChatModel = dao.detail(modelId);
        Preconditions.checkNotNull(aiChatModel, "model is null for {}", modelId);
        if(chatOptions == null){
            chatOptions = new DefaultChatOptions();
        }
        chatOptions.setModel(aiChatModel.getModel());
        if(chatOptions.getTemperature() == null){
            chatOptions.setTemperature(aiChatModel.getTemperature());
        }
        return AiChatModelFactory.instance(aiChatModel.getProviderId(), aiChatModel.getBaseUrl(), aiChatModel.getApiKey(), chatOptions)
                .of();
    }

    public static ChatModel of(final String modelId){
        Preconditions.checkNotBlank(modelId, "modelId is blank.");
        final AiChatModelDao dao = SpringUtil.getBean(AiChatModelDao.class);
        final AiChatModel aiChatModel = dao.detail(modelId);
        Preconditions.checkNotNull(aiChatModel, "model is null for {}", modelId);
        final DefaultChatOptions chatOptions = new DefaultChatOptions();
        chatOptions.setModel(aiChatModel.getModel());
        chatOptions.setTemperature(aiChatModel.getTemperature());
        return AiChatModelFactory.instance(aiChatModel.getProviderId(), aiChatModel.getBaseUrl(), aiChatModel.getApiKey(), chatOptions)
                .of();
    }
}
