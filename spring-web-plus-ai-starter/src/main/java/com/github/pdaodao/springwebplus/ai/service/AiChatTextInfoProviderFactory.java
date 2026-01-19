package com.github.pdaodao.springwebplus.ai.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.AiChatTextInfoProvider;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AiChatTextInfoProviderFactory {
    private final List<AiChatTextInfoProvider> providerList;
    public AiChatTextInfoProvider byType(final String type){
        Preconditions.checkArgument(CollUtil.isNotEmpty(providerList), "AiChatTextInfoProvider is empty");
        for(final AiChatTextInfoProvider p: providerList){
            if(StrUtil.equalsIgnoreCase(type, p.type())){
                return p;
            }
        }
        Preconditions.assertTrue(true, "AiChatTextInfoProvider not found for {}", type);
        return null;
    }
}
