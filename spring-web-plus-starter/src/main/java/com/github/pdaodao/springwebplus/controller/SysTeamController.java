package com.github.pdaodao.springwebplus.controller;

import com.github.pdaodao.springwebplus.base.pojo.CurrentUserInfo;
import com.github.pdaodao.springwebplus.base.pojo.MemberType;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.entity.SysTeam;
import com.github.pdaodao.springwebplus.entity.SysTeamUser;
import com.github.pdaodao.springwebplus.service.SysTeamService;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "团队管理")
@RequestMapping(Constant.ApiPrefix + "/team")
@AllArgsConstructor
public class SysTeamController {
    private final SysTeamService sysTeamService;

    @GetMapping("my")
    @Operation(summary = "我参与的团队")
    public List<SysTeam> myTeams(final CurrentUserInfo currentUserInfo){
        final List<SysTeam> list = sysTeamService.userTeams(currentUserInfo.getId());
        return list;
    }

    @GetMapping("save")
    @Operation(summary = "保存团队信息")
    public SysTeam save(@RequestBody SysTeam sysTeam){
        return sysTeamService.save(sysTeam);
    }

    @PostMapping("delete")
    @Operation(summary = "删除团队")
    public Boolean delete(@Parameter(name = "id", description = "团队id") final String id) {
        return sysTeamService.deleteById(id);
    }

    @GetMapping("users")
    @Operation(summary = "团队的用户列表")
    public PageResult<SysTeamUser> teamUsers(@Parameter(name = "id", description = "团队id") final String id,
                                             final PageRequestParam pageRequestParam){
        try (final PageHelper pageHelper = PageHelper.startPage(pageRequestParam)) {
            final List<SysTeamUser> list = sysTeamService.teamUsers(id, pageRequestParam.getQ());
            return pageHelper.toPageResult(list);
        }
    }

    @PostMapping("addUser")
    @Operation(summary = "添加用户")
    public SysTeamUser addTeamUser(@RequestBody @Valid SysTeamUser sysTeamUser){
        if(sysTeamUser.getMemberType() == null){
            sysTeamUser.setMemberType(MemberType.use);
        }
        return sysTeamService.addTeamUser(sysTeamUser);
    }

    @PostMapping("updateUser")
    @Operation(summary = "修改用户成员类型")
    public SysTeamUser updateTeamUser(@RequestBody @Valid SysTeamUser sysTeamUser){
        if(sysTeamUser.getMemberType() == null){
            sysTeamUser.setMemberType(MemberType.use);
        }
        return sysTeamService.addTeamUser(sysTeamUser);
    }

    @PostMapping("deleteUser")
    @Operation(summary = "删除成员")
    public Boolean removeTeamUser(@Parameter(name = "id", description = "成员id") final String id){
        return sysTeamService.removeTeamUser(id);
    }
}
