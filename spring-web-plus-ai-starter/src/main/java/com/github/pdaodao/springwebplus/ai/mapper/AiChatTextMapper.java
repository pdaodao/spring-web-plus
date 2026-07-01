package com.github.pdaodao.springwebplus.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiChatTextMapper extends BaseMapper<AiChatText> {
    List<AiChatText> infoList(final AiChatTextQuery query);
}