package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.dao.AiChatAppDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatDoc;
import com.github.pdaodao.springwebplus.ai.pojo.AiChatType;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "场景管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/app")
public class AiChatAppController {
    private final AiChatAppDao appDao;

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<AiChatApp> list() {
        return appDao.list(QueryBuilder.lambda(AiChatApp.class)
                .eq(AiChatApp::getTeamId, RequestUtil.getTeamOrDefault())
                .build().orderByAsc(AiChatApp::getSeq));
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatApp info(final String id) {
        return appDao.info(id);
    }

    @GetMapping("dicTree")
    @Operation(summary = "可选的文档列表树")
    public List<AiChatDoc> docTree(@Parameter(description = "问答类型(大模型llm,sql问答sql)") @RequestParam(required = false) final AiChatType chatType) {
        return null;
    }


    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) {
        return appDao.removeById(idWrap.getId());
    }
}
