package com.github.apengda.springwebplus.controller;

import cn.hutool.system.SystemUtil;
import com.github.apengda.springwebplus.ext.ServerInfoUtil;
import com.github.apengda.springwebplus.ext.pojo.ProjectInfo;
import com.github.apengda.springwebplus.ext.pojo.ServerInfo;
import com.github.apengda.springwebplus.starter.auth.Permission;
import com.github.apengda.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "服务器信息")
@RequestMapping(Constant.ApiPrefix + "/server")
@AllArgsConstructor
public class ServerInfoController {
    private final Environment environment;

    @Operation(summary = "服务器信息详情")
    @GetMapping("info")
    @Permission("server:info")
    public ServerInfo getServerInfo() {
        ServerInfo serverInfo = ServerInfoUtil.getServerInfo();
        // 获取项目信息
        // 项目名称
        String name = environment.getProperty("spring.application.name");
        // 端口号
        String port = environment.getProperty("server.port");
        // 上下文路径
        String contextPath = environment.getProperty("server.servlet.context-path");
        // 激活的环境
        String active = environment.getProperty("spring.profiles.active");
        // 当前项目路径
        String userDir = SystemUtil.get("user.dir");
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setName(name);
        projectInfo.setPort(port);
        projectInfo.setContextPath(contextPath);
        projectInfo.setActive(active);
        projectInfo.setUserDir(userDir);
        serverInfo.setProjectInfo(projectInfo);
        return serverInfo;
    }
}
