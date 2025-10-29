package com.github.pdaodao.springwebplus.ai.dao;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDocItem;
import com.github.pdaodao.springwebplus.ai.mapper.AiChatDocMapper;
import com.github.pdaodao.springwebplus.ai.pojo.ChatDocNamespace;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class AiChatDocDao extends BaseDao<AiChatDocMapper, AiChatDoc> {
    @Autowired
    private AiChatDocItemDao itemDao;

    public List<AiChatDoc> list(final AiChatDocQuery query) {
        return list(QueryBuilder.lambda(AiChatDoc.class)
                .eq(AiChatDoc::getTeamId, query.getTeamId())
                .eq(AiChatDoc::getIsDir, query.getIsDir())
                .selectExclude("sql_text").build());
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveRich(final AiChatDoc aiChatDoc){
        //1. 先保存基本信息
         save(aiChatDoc);
        if (BooleanUtil.isTrue(aiChatDoc.getIsDir())) {
            return false;
        }
        // 文件文档 由于文本块较多 采用单个保存 这里就直接返回
        if (StrUtil.equals(ChatDocNamespace.file, aiChatDoc.getNamespace())
                || CollUtil.isEmpty(aiChatDoc.getDocItems())) {
            return false;
        }
        final String type = aiChatDoc.itemType();
        for (final AiChatDocItem item : aiChatDoc.getDocItems()) {
            item.setType(type);
            item.setDocId(aiChatDoc.getId());
        }
        itemDao.removeByDocId(aiChatDoc.getId());
        itemDao.saveBatch(aiChatDoc.getDocItems());
        return true;
    }
}