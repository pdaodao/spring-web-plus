package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.entity.AiChatTermText;
import com.github.pdaodao.springwebplus.ai.service.AiChatTermTextService;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@Tag(name = "术语管理")
@RestController
@RequestMapping(Constant.ChatApiPrefix + "/term-text")
@AllArgsConstructor
public class AiChatTermTextController {
    private final AiChatTermTextService termTextService;

    @GetMapping("check-title")
    @Operation(summary = "名称是否可用")
    public Boolean checkTitleExist(@Schema(description = "id") @RequestParam(required = false) final String id,
                                   @Schema(description = "名称") @RequestParam final String title) {
        Preconditions.checkNotBlank(title, "请指定名称");
        return termTextService.checkDistinctTitle(RequestUtil.getTeamOrDefault(), id, title);
    }

    @GetMapping("/list")
    @Operation(summary = "分页")
    public List<AiChatTermText> page(final PageRequestParam pageRequestParam) {
        PageHelper.startPage(pageRequestParam);
        final List<AiChatTermText> list = termTextService.infoList(RequestUtil.getTeamId(), pageRequestParam.getQ());
        return list;
    }

    @PostMapping("/save")
    @Operation(summary = "分页")
    public AiChatTermText save(@Validated @RequestBody final AiChatTermText text) throws Exception{
        termTextService.save(text);
        return text;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatTermText info(final String id) throws Exception{
        return termTextService.info(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) throws Exception{
        return termTextService.deleteById(idWrap.getId());
    }
}