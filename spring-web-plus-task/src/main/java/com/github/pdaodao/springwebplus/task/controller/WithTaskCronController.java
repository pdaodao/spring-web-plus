package com.github.pdaodao.springwebplus.task.controller;

import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskInfoEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.task.service.WithTaskCronService;
import com.github.pdaodao.springwebplus.tool.task.CronUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class WithTaskCronController<T extends ZtTaskInfoEntity> {
    protected abstract WithTaskCronService<T> jobCronService();

    @Operation(summary = "切换调度状态")
    @GetMapping("toggle")
    public Boolean cronToggle(final Long id, final Boolean enabled){
        return jobCronService().toggleCron(id, enabled);
    }

    @Operation(summary = "任务详情")
    @GetMapping("info")
    public T info(final Long id){
        return jobCronService().info(id);
    }

    @Operation(summary = "保存调度信息-返回未来执行时间列表")
    @PostMapping("/save-cron")
    public List<String> saveCron(@RequestBody ZtTaskInfoEntity entity) throws Exception{
        Preconditions.checkNotNull(entity.getId(), "任务id不能为空.");
        Preconditions.checkNotNull(entity.getCronSetting(), "调度信息配置不能为空.");
        jobCronService().saveCron(entity);
        Date now = DateTimeUtil.now();
        final List<String> ret = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            final Date next = CronUtil.nextTime(entity.getCronSetting(), now);
            now = next;
            ret.add(DateTimeUtil.formatDateTime(next));
        }
        return ret;
    }

    @Operation(summary = "运行记录")
    @GetMapping("log")
    public List<ZtTaskLogEntity> logs(final TaskLogQuery query){
        PageHelper.startPage(query);
        final List<ZtTaskLogEntity> logs = jobCronService().logList(query);
        return logs;
    }

//    @Operation(summary = "提交任务运行")
//    @GetMapping("/submit")
//    public Long submit(final ZtJobInfo jobInfo){
//        if(StrUtil.isBlank(jobInfo.getJobType())){
//            jobInfo.setJobType(jobType());
//        }
//        return jobCronService().jobSubmitter().submit(jobInfo);
//    }
//
//    @Operation(summary = "获取任务日志")
//    @GetMapping("/getLog")
//    public LogResult getLog(final JobDataLogRequest request) {
//        final XxlJobManager m = SpringUtil.getBean(XxlJobManager.class);
//        Preconditions.checkNotNull(m, "任务管理器不存在.");
//        try {
//            return m.getLog(request);
//        } catch (Exception e) {
//            return new LogResult();
//        }
//    }
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
