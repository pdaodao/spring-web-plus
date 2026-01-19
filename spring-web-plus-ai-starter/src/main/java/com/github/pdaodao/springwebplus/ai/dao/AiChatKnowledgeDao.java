package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledge;
import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledgeChunk;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatKnowledgeMapper;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocNamespace;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AiChatKnowledgeDao extends BaseDao<AiChatKnowledgeMapper, AiChatKnowledge> {
    @Autowired
    private AiChatKnowledgeChunkDao itemDao;

    public List<AiChatKnowledge> list(final AiChatDocQuery query) {
        return list(QueryBuilder.lambda(AiChatKnowledge.class)
                .eq(AiChatKnowledge::getTeamId, query.getTeamId())
                .eq(AiChatKnowledge::getIsDir, query.getIsDir())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveRich(final AiChatKnowledge aiChatDoc){
        //1. 先保存基本信息
         save(aiChatDoc);
        if (BooleanUtil.isTrue(aiChatDoc.getIsDir())) {
            return false;
        }
//        // 文件文档 由于文本块较多 采用单个保存 这里就直接返回
//        if (StrUtil.equals(ChatDocNamespace.file, aiChatDoc.getNamespace())
//                || CollUtil.isEmpty(aiChatDoc.getDocItems())) {
//            return false;
//        }
//        final String type = aiChatDoc.itemType();
//        for (final AiChatKnowledgeChunk item : aiChatDoc.getDocItems()) {
//            item.setType(type);
//            item.setDocId(aiChatDoc.getId());
//        }
//        itemDao.removeByDocId(aiChatDoc.getId());
//        itemDao.saveBatch(aiChatDoc.getDocItems());
        return true;
    }
}