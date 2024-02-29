package com.github.apengda.springwebplus.controller;

import com.github.apengda.springwebplus.dao.SysLogDao;
import com.github.apengda.springwebplus.entity.SysLog;
import com.github.apengda.springwebplus.starter.pojo.PageR;
import com.github.apengda.springwebplus.starter.pojo.PageRequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "测试")
@RequestMapping("/api/v1/test")
@AllArgsConstructor
public class TestController {
    private SysLogDao sysLogDao;

    @Operation(summary = "hello")
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("logs")
    public PageR<SysLog> logs(PageRequestParam pageRequestParam) {
        return sysLogDao.listPage(null);
    }

}
