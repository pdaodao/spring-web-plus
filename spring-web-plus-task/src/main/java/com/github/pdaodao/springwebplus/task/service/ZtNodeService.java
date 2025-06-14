package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.http.HttpUtil;
import com.github.pdaodao.springwebplus.base.util.ExceptionUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskLogDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskNodeEntity;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.TaskStatus;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZtNodeService {
    private final ZtTaskNodeRegistService registService;
    private final ZtTaskLogDao logDao;

    /**
     * 任务中心触发任务到执行器
     * @param cronTaskInfo
     * @return
     */
    public Long triggerByAdmin(final CronTaskInfo cronTaskInfo) throws Exception{
        Preconditions.checkNotNull(cronTaskInfo.getTaskId(), "task-id is null.");
        final ZtTaskNodeEntity node = registService.executorNode(cronTaskInfo.getTaskId());
        final ZtTaskLogEntity log = new ZtTaskLogEntity();
        log.setTaskId(cronTaskInfo.getTaskId());
        log.setNodeId(node.getId());
        log.setTaskStatus(TaskStatus.running);
        log.setParams(cronTaskInfo.getParams());
        log.setIsCron(cronTaskInfo.getIsCron());
        logDao.save(log);
        try{
            final String url = node.getUrl()+"/node/api/v1/executor/submit";
            for(int i = 0; i < 3; i++){
                try{
                    final String ret = HttpUtil.createPost(url)
                            .header("whoami", node.getAccess())
                            .body(JsonUtil.toJsonString(cronTaskInfo)).execute().body();
                    System.out.println("hello");
                    break;
                }catch (Exception e){
                    if(i >= 2){
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
}
