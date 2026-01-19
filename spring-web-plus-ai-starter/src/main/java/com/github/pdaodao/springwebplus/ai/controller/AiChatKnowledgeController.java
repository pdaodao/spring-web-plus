package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.entity.AiChatKnowledge;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.ai.service.AiChatKnowledgeService;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
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
@Tag(name = "知识库管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/knowledge")
public class AiChatKnowledgeController {
    private final AiChatKnowledgeService docService;

    @GetMapping("list")
    @Operation(summary = "文档列表")
    public List<AiChatKnowledge> list(final AiChatDocQuery query) {
        query.setTeamId(RequestUtil.getTeamId());
        PageHelper.startPage(query);
        return docService.list(query);
    }

    @GetMapping("tree")
    @Operation(summary = "文档列表树")
    public List<AiChatKnowledge> listTree(final AiChatDocQuery query) {
        query.setTeamId(RequestUtil.getTeamId());
        final List<AiChatKnowledge> list = docService.list(query);
        return IdUtil.toTree(list, AiChatKnowledge::getId, AiChatKnowledge::getPid);
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatKnowledge info(final String id) {
        final AiChatKnowledge doc = docService.info(id);
        Preconditions.checkNotNull(doc, "不存在该数据.");
        return doc;
    }

    @GetMapping("search")
    @Operation(summary = "search")
    public List<AiEmbedText> search(final String key, final Double score) throws Exception {
        return docService.search(key, score);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatKnowledge saveInfo(@RequestBody AiChatKnowledge aiChatDoc) throws Exception {
        Preconditions.checkNotBlank(aiChatDoc.getTitle(), "标题不能为空");
        docService.saveInfo(aiChatDoc);
        return aiChatDoc;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> wrap) throws Exception {
        return docService.delete(wrap.getId());
    }
}