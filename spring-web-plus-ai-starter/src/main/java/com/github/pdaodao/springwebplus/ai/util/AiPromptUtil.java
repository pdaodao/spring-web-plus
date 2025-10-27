package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.map.MapUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatPromptDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatPrompt;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.util.Map;

public class AiPromptUtil {
    /**
     * 获取数据库中存储的提示词
     * @param id
     * @return
     */
    public static String get(final String id){
        final AiChatPromptDao dao = SpringUtil.getBean(AiChatPromptDao.class);
        final AiChatPrompt p = dao.getById(id);
        return p == null ? null : p.getContent();
    }


    /**
     * 提示词 和 变量进行拼装
     * @param id    提示词id
     * @param map   变量
     * @return
     */
    public static String concatById(final String id, final Map<String, String> map) {
        String p = get(id);
        Preconditions.checkNotBlank(p, "prompt is blank for {}", id);
        if(MapUtil.isEmpty(map)){
            return p;
        }
        for(final Map.Entry<String, String> entry: map.entrySet()){
            p = p.replace("#"+entry.getKey(), entry.getValue());
        }
        return p;
    }

    /**
     * 提示词 和 变量进行拼装
     * @param prompt   提示词
     * @param map      变量
     * @return
     */
    public static String concat(String prompt, final Map<String, String> map) {
        Preconditions.checkNotBlank(prompt, "prompt is blank");
        if(MapUtil.isEmpty(map)){
            return prompt;
        }
        for(final Map.Entry<String, String> entry: map.entrySet()){
            prompt = prompt.replace("#"+entry.getKey(), entry.getValue());
        }
        return prompt;
    }
}