package com.github.pdaodao.springwebplus.ai.util;

import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.service.AiVectorStoreService;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import java.util.List;

/**
 * 向量存储检索工具类
 */
public class AiTextStoreUtil {

    public static boolean isEmpty() {
        return SpringUtil.getBean(AiVectorStoreService.class).isEmpty();
    }

    public static void save(final String teamId, final AiChatText text) {
        SpringUtil.getBean(AiVectorStoreService.class)
                .save(teamId, text);
    }

    public static void saveBatch(final String teamId, final List<AiChatText> list){
        SpringUtil.getBean(AiVectorStoreService.class)
                .saveBatch(teamId, list);
    }

    public static void deleteById(final String id, final String namespace){
        SpringUtil.getBean(AiVectorStoreService.class)
                .deleteById(id, namespace);
    }

    public static List<AiChatText> search(final String teamId, final String q) throws Exception {
        return SpringUtil.getBean(AiVectorStoreService.class)
                .search(teamId, q);
    }
}