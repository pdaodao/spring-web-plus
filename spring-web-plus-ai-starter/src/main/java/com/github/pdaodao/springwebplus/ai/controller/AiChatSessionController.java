package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatSessionDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatSessionMsgDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSession;
import com.github.pdaodao.springwebplus.ai.entity.AiChatSessionMsg;
import com.github.pdaodao.springwebplus.ai.query.AiChatSessionQuery;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "聊天会话管理")
@RestController
@RequestMapping(Constant.ChatApiPrefix + "/session")
@AllArgsConstructor
public class AiChatSessionController {
    private final AiChatSessionDao sessionDao;
    private final AiChatSessionMsgDao sessionMsgDao;

    @GetMapping("list")
    @Operation(summary = "用户的session列表")
    public List<AiChatSession> list(final AiChatSessionQuery query) {
        query.setUserId(RequestUtil.getUserId());
        query.setTeamId(RequestUtil.getTeamOrDefault());
        PageHelper.startPage(query);
        return sessionDao.list(query);
    }

    @GetMapping("msgs")
    @Operation(summary = "会话消息列表")
    public List<AiChatSessionMsg> msgList(@Parameter(description = "会话id") final String sessionId) {
        return sessionMsgDao.listBySession(sessionId);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatSession save(@Validated @RequestBody AiChatSession entity) {
        entity.setUserId(RequestUtil.getUserId());
        sessionDao.save(entity);
        return entity;
    }

    @PostMapping("retitle")
    @Operation(summary = "修改标题")
    public AiChatSession reTitle(@Validated @RequestBody AiChatSession entity) {
        Preconditions.checkNotNull(entity.getId(), "主键不能为空.");
        Preconditions.checkNotBlank(entity.getTitle(), "标题不能为空.");
        final AiChatSession session = sessionDao.getById(entity.getId());
        Preconditions.checkNotNull(session);
        Preconditions.checkArgument(StrUtil.equals(RequestUtil.getUserId(), session.getUserId()), "非本人的无法修改.");
        session.setTitle(entity.getTitle());
        sessionDao.save(session);
        return session;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Parameter(name = "id", description = "id") String id) {
        final AiChatSession session = sessionDao.getById(id);
        if (session == null) {
            return true;
        }
        Preconditions.checkArgument(StrUtil.equals(RequestUtil.getUserId(), session.getUserId()), "非本人的无法删除.");
        return sessionDao.removeById(id);
    }
}