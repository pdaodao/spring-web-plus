package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.dao.AiChatTopicDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTopic;
import com.github.pdaodao.springwebplus.ai.query.AiChatTopicQuery;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Tag(name = "知识库分类主题管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/topic")
public class AiChatTopicController {
    private final AiChatTopicDao dao;

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<AiChatTopic> list(final AiChatTopicQuery query) {
        query.setTeamId(RequestUtil.getTeamId());
        return dao.infoList(query);
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatTopic info(final String id) {
        final AiChatTopic doc = dao.getById(id);
        return doc;
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatTopic saveInfo(@RequestBody AiChatTopic aiChatDoc) throws Exception {
        Preconditions.checkNotBlank(aiChatDoc.getTitle(), "标题不能为空");
        dao.save(aiChatDoc);
        return aiChatDoc;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> wrap) throws Exception {
        return dao.delete(wrap.getId());
    }
}