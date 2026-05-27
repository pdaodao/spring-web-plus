package com.github.pdaodao.springwebplus.task.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskCronQuery;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.task.service.WithTaskCronService;
import com.github.pdaodao.springwebplus.task.service.ZtExecutorService;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.CronUtil;
import com.github.pdaodao.springwebplus.tool.task.LogResult;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class WithTaskCronController<T extends ZtTaskCronEntity> {
    protected abstract WithTaskCronService<T> jobCronService();

    @GetMapping("tree")
    @Operation(summary = "任务树")
    public List<ZtTaskCronEntity> tree(){
        final TaskCronQuery query = new TaskCronQuery();
        query.setTeamId(RequestUtil.getTeamId());
        final List<ZtTaskCronEntity> list = jobCronService().list(query);
        return IdUtil.toTree(list, ZtTaskCronEntity::getId, ZtTaskCronEntity::getPid);
    }

    @GetMapping("toggle")
    @Operation(summary = "切换调度状态")
    public Boolean cronToggle(final String id, final Boolean enabled){
        return jobCronService().toggleCron(id, enabled);
    }

    @GetMapping("info")
    @Operation(summary = "任务详情")
    public T info(final String id){
        return jobCronService().info(id);
    }

    @PostMapping("mkDir")
    @Operation(summary = "保存分类")
    public T mkDir(@RequestBody T body) throws Exception{
        body.setIsDir(true);
        jobCronService().save(body);
        return body;
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public T save(@RequestBody T body) throws Exception{
        jobCronService().save(body);
        return body;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody final IdWrap<String> idWrap){
        return jobCronService().delete(idWrap.getId());
    }

    @PostMapping("/save-cron")
    @Operation(summary = "保存调度信息-返回未来执行时间列表")
    public List<String> saveCron(@RequestBody ZtTaskCronEntity entity) throws Exception{
        Preconditions.checkNotNull(entity.getId(), "任务id不能为空.");
        Preconditions.checkNotNull(entity.getCronSetting(), "调度信息配置不能为空.");
        jobCronService().saveCron(entity);
        LocalDateTime now = DateTimeUtil.now();
        final List<String> ret = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            final LocalDateTime next = CronUtil.nextTime(entity.getCronSetting(), now);
            now = next;
            ret.add(DateTimeUtil.formatDateTime(next));
        }
        return ret;
    }

    @Operation(summary = "提交任务运行")
    @PostMapping("/submit")
    public String submit(final @RequestBody CronTaskInfo taskInfo) throws Exception{
        final ZtExecutorService nodeService = SpringUtil.getBean(ZtExecutorService.class);
        final String runtimeId = nodeService.triggerByAdmin(taskInfo);
        ThreadUtil.safeSleep(300);
        return runtimeId;
    }

    @Operation(summary = "运行记录")
    @GetMapping("log")
    public List<ZtTaskLogEntity> logs(final TaskLogQuery query){
        PageHelper.startPage(query);
        final List<ZtTaskLogEntity> logs = jobCronService().logList(query);
        return logs;
    }

    @Operation(summary = "获取任务日志文本")
    @GetMapping("/getLog")
    public LogResult getLog(final String logId, final Integer from) {
        final ZtExecutorService nodeService = SpringUtil.getBean(ZtExecutorService.class);
        return nodeService.getLog(logId, from);
    }


//
//    @Operation(summary = "关闭任务")
//    @GetMapping("/stop")
//    public Boolean stop(final ZtJobInfo jobInfo){
//        if(StrUtil.isBlank(jobInfo.getJobType())){
//            jobInfo.setJobType(jobType());
//        }
//        return jobCronService().jobSubmitter().stop(jobInfo);
//    }
}
