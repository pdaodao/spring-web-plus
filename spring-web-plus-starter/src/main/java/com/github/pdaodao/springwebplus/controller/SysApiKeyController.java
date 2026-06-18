package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.pojo.WithTimeQuery;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.dao.SysApiKeyDao;
import com.github.pdaodao.springwebplus.dao.SysLogDao;
import com.github.pdaodao.springwebplus.dao.SysLoginLogDao;
import com.github.pdaodao.springwebplus.entity.SysApiKey;
import com.github.pdaodao.springwebplus.entity.SysLog;
import com.github.pdaodao.springwebplus.entity.SysLoginLog;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "apiKey管理")
@RequestMapping(Constant.ApiPrefix + "/apiKey")
@AllArgsConstructor
public class SysApiKeyController {
    private final SysApiKeyDao apiKeyDao;

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<SysApiKey> list(final String teamId, final PageRequestParam pageRequestParam){
        PageHelper.startPage(pageRequestParam);
        return apiKeyDao.infoList(teamId);
    }
}
