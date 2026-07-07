package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.pojo.WithTimeQuery;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.dao.SysLogDao;
import com.github.pdaodao.springwebplus.dao.SysLoginLogDao;
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
@Tag(name = "用户操作记录")
@RequestMapping(Constant.ApiPrefix + "/log")
@AllArgsConstructor
public class SysLogController {
    private final SysLogDao logDao;
    private final SysLoginLogDao loginLogDao;

    @GetMapping("list")
    @Operation(summary = "操作记录")
    public List<SysLog> logList(final WithTimeQuery query){
        query.defaultOrderBy("id", false);
        PageHelper.startPage(query);
        final List<SysLog> list = logDao.list(QueryBuilder.lambda(SysLog.class)
                .like(query.getQ(), SysLog::getModule, SysLog::getDescription, SysLog::getUserNickname)
                .ge(SysLog::getCreateTime, query.getStartDate())
                .le(SysLog::getCreateTime, query.getEndDate()).build());
        return list;
    }

    @GetMapping("login-list")
    @Operation(summary = "登陆记录")
    public List<SysLoginLog> loginList(final WithTimeQuery query){
        PageHelper.startPage(query);
        final List<SysLoginLog> list = loginLogDao.list(QueryBuilder.lambda(SysLoginLog.class)
                .ge(SysLoginLog::getCreateTime, query.getStartDate())
                .le(SysLoginLog::getCreateTime, query.getEndDate()).build());
        return list;
    }
}
