package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.service.SysTeamService;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "团队")
@RequestMapping(Constant.ApiPrefix + "/team")
@AllArgsConstructor
public class SysTeamController {
    private final SysTeamService sysTeamService;

    @GetMapping("my")
    @Operation(summary = "我参与的团队")
    public List<SysTeam> myTeams(final CurrentUserInfo currentUserInfo){
        final List<SysTeam> list = sysTeamService.userTeams(currentUserInfo.getId(), null);
        return list;
    }

    @GetMapping("my/{namespace}")
    @Operation(summary = "授权资源的团队")
    public List<SysTeam> myTeamsOfResource(@PathVariable("namespace") final String namespace, final CurrentUserInfo currentUserInfo){
        final List<SysTeam> list = sysTeamService.userTeams(currentUserInfo.getId(), namespace);
        return list;
    }
}
