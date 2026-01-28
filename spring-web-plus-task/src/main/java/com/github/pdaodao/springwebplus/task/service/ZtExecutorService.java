package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.task.config.ZtTaskExecutorConfig;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskLogDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskExecutorEntity;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.LogResult;
import com.github.pdaodao.springwebplus.tool.task.TaskFactory;
import com.github.pdaodao.springwebplus.tool.task.TaskStatus;
import com.github.pdaodao.springwebplus.tool.task.core.TaskRunnable;
import com.github.pdaodao.springwebplus.tool.task.core.TaskThreadPoolFactory;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ZtExecutorService {
    private final ZtExecutorRegistService registService;
    private final ZtTaskLogDao logDao;
    private final TaskFactory taskFactory;
    private final ZtTaskExecutorConfig executorConfig;

    /**
     * 任务中心触发任务到执行器
     * @param cronTaskInfo
     * @return  任务运行id
     */
    public String triggerByAdmin(final CronTaskInfo cronTaskInfo) throws Exception{
        Preconditions.checkNotNull(cronTaskInfo.getTaskId(), "task-id is null.");
        final ZtTaskExecutorEntity node = registService.executorNode(cronTaskInfo.getTaskId());
        final ZtTaskLogEntity log = new ZtTaskLogEntity();
        log.setTaskId(cronTaskInfo.getTaskId());
        log.setNodeId(node.getId());
        log.setTaskStatus(TaskStatus.running);
        log.setParams(cronTaskInfo.getParams());
        log.setIsCron(cronTaskInfo.getIsCron());
        logDao.save(log);
        try{
            final String url = node.getUrl()+"/executor/api/v1/task/submit";
            for(int i = 0; i < 3; i++){
                try{
                    final String ret = HttpUtil.createPost(url)
                            .header("whoami", node.getAccess())
                            .body(JsonUtil.toJsonString(cronTaskInfo)).execute().body();
                    System.out.println("hello");
                    break;
                }catch (Exception e){
                    if(i == 2){
                        throw e;
                    }
                }
            }
        }catch (Exception e){
            log.setTaskStatus(TaskStatus.failed);
            log.setError("提交到执行器失败:"+ ExceptionUtil.getSimpleMsg(e));
            throw e;
        }
        return log.getId();
    }

    public LogResult getLog(final String logId, final Integer from){
        final ZtTaskLogEntity log = logDao.getById(logId);
        Preconditions.checkNotNull(log, "任务运行记录不存在.");
        Preconditions.checkNotBlank(log.getNodeId(), "任务运行执行器为空.");
        final ZtTaskExecutorEntity nodeEntity = registService.getById(log.getNodeId());
        Preconditions.checkNotNull(nodeEntity, "执行器不存在.");
        final Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("logId", logId);
        paramMap.put("from", from);
        try{
            final String url = nodeEntity.getUrl()+"/executor/api/v1/task/getLog";
            for(int i = 0; i < 3; i++){
                try{
                    final String ret = HttpUtil.createGet(url)
                            .form(paramMap)
                            .header("whoami", nodeEntity.getAccess())
                            .execute().body();
                    return JsonUtil.objectMapper.readValue(ret, LogResult.class);
                }catch (Exception e){
                    if(i == 2){
                        throw e;
                    }
                }
            }
        }catch (Exception e){
        }
        return new LogResult();
    }


    public void doExecute(final CronTaskInfo taskInfo){
        Preconditions.checkNotNull(taskInfo.getTaskId(), "任务id为空.");
        final ZtTaskLogEntity logEntity = new ZtTaskLogEntity();
        logEntity.setId(taskInfo.getLogId());
        if(StrUtil.isBlank(taskInfo.getLogId())){
            logEntity.setTaskId(taskInfo.getTaskId());
            logEntity.setTaskStatus(TaskStatus.running);
            logEntity.setIsCron(taskInfo.getIsCron());
            logEntity.setNodeId(executorConfig.getNodeId());
            logDao.save(logEntity);
            taskInfo.setLogId(logEntity.getId());
        }
        try{
            final TaskRunnable taskRunnable = taskFactory.executor(taskInfo);
            Preconditions.checkNotNull(taskRunnable, "不支持该任务运行{}", taskInfo.getTaskType());
            TaskThreadPoolFactory.ofBig()
                    .execute(taskRunnable);
        }catch (Exception e){
            logEntity.setTaskStatus(TaskStatus.failed);
            logEntity.setError(ExceptionUtil.getSimpleMsg(e));
            logDao.save(logEntity);
        }
    }
}
