package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskCronDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskCronQuery;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

public abstract class WithTaskCronService<T extends ZtTaskCronEntity>{
    /**
     * 切换调度
     * @param id
     * @param cronEnabled
     * @return
     */
    public Boolean toggleCron(final String id, final Boolean cronEnabled){
        return taskCronDao().toggleCron(id, cronEnabled);
    }

    public List<ZtTaskLogEntity> logList(final TaskLogQuery query){
        return taskCronDao().logList(query);
    }

    public List<ZtTaskCronEntity> list(final TaskCronQuery query){
        query.setNamespace(namespace());
        return taskCronDao().list(query);
    }


    /**
     * 任务详情
     * @return
     */
    public T info(final String id){
        Preconditions.checkNotNull(id, " task-id is null.");
        final ZtTaskCronEntity cronInfo = taskCronDao().getById(id);
        if(cronInfo == null){
            return null;
        }
        return taskContent(cronInfo);
    }

    public Boolean saveCron(@RequestBody ZtTaskCronEntity entity) throws Exception{
        return taskCronDao().saveCron(entity);
    }

    protected abstract String namespace();

    /**
     * 获取任务内容
     * @param taskInfo
     * @return
     */
    protected abstract T taskContent(final ZtTaskCronEntity taskInfo);

    protected ZtTaskCronDao taskCronDao(){
        return SpringUtil.getBean(ZtTaskCronDao.class);
    }
}
