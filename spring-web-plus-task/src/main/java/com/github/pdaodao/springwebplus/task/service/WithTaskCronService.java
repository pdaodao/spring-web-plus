package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskInfoDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskInfoEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

public abstract class WithTaskCronService<T extends ZtTaskInfoEntity>{
    /**
     * 切换调度
     * @param id
     * @param cronEnabled
     * @return
     */
    public Boolean toggleCron(final Long id, final Boolean cronEnabled){
        return taskInfoDao().toggleCron(id, cronEnabled);
    }

    public List<ZtTaskLogEntity> logList(final TaskLogQuery query){
        return taskInfoDao().logList(query);
    }

    /**
     * 任务详情
     * @return
     */
    public T info(final Long id){
        Preconditions.checkNotNull(id, " task-id is null.");
        final ZtTaskInfoEntity taskInfo = taskInfoDao().getById(id);
        if(taskInfo == null){
            return null;
        }
        return taskContent(taskInfo);
    }

    public Boolean saveCron(@RequestBody ZtTaskInfoEntity entity) throws Exception{
        return taskInfoDao().saveCron(entity);
    }

    /**
     * 获取任务内容
     * @param taskInfo
     * @return
     */
    protected abstract T taskContent(final ZtTaskInfoEntity taskInfo);

    protected ZtTaskInfoDao taskInfoDao(){
        return SpringUtil.getBean(ZtTaskInfoDao.class);
    }
}
