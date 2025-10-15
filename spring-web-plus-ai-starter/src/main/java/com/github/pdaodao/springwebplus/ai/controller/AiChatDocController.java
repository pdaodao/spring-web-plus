package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.query.AiChatDocQuery;
import com.github.pdaodao.springwebplus.ai.service.AiChatDocService;
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
@Tag(name = "知识文档管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/doc")
public class AiChatDocController {
    private final AiChatDocService docService;

    @GetMapping("list")
    @Operation(summary = "文档列表")
    public List<AiChatDoc> list(final AiChatDocQuery query) {
        query.setTeamId(RequestUtil.getTeamId());
        PageHelper.startPage(query);
        return docService.list(query);
    }

    @GetMapping("tree")
    @Operation(summary = "文档列表树")
    public List<AiChatDoc> listTree(final AiChatDocQuery query) {
        query.setTeamId(RequestUtil.getTeamId());
        final List<AiChatDoc> list = docService.list(query);
        return IdUtil.toTree(list, AiChatDoc::getId, AiChatDoc::getPid);
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatDoc info(final String id) {
        final AiChatDoc doc = docService.info(id);
        Preconditions.checkNotNull(doc, "不存在该数据.");
        return doc;
    }

    @PostMapping("save")
    @Operation(summary = "详情")
    public AiChatDoc saveInfo(@RequestBody AiChatDoc aiChatDoc) throws Exception {
        Preconditions.checkNotBlank(aiChatDoc.getTitle(), "标题不能为空");
        Preconditions.checkNotNull(aiChatDoc.getDocNamespace(), "类型不能为空");
        docService.saveInfo(aiChatDoc);
        return aiChatDoc;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> wrap) throws Exception {
        return docService.delete(wrap.getId());
    }
}