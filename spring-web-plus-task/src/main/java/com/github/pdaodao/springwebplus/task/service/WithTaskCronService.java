package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskCronDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskCronQuery;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;

public abstract class WithTaskCronService<T extends ZtTaskCronEntity>{
    /**
     * 切换调度
     * @param id
     * @param cronEnabled
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean toggleCron(final String id, final Boolean cronEnabled){
        toggleEnabled(id, cronEnabled);
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
        T ret = taskContent(cronInfo);
        if(ret != null && cronInfo.getCronSetting() != null && cronInfo.getNextTime() != null){
            ret.setNextTime(cronInfo.getNextTime());
        }
        return ret;
    }

    public Boolean saveCron(@RequestBody ZtTaskCronEntity entity) throws Exception{
        return taskCronDao().saveCron(entity);
    }

    protected abstract String namespace();


    protected ZtTaskCronDao taskCronDao(){
        return SpringUtil.getBean(ZtTaskCronDao.class);
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(T body) throws Exception{
        body.setNamespace(namespace());
        if(BooleanUtil.isTrue(body.getIsDir())){
            if(StrUtil.isNotBlank(body.getPid()) && StrUtil.equals("0", body.getPid())){
                final ZtTaskCronEntity p = taskCronDao().getById(body.getPid());
                Preconditions.checkNotNull(p, "父分类不存在");
                Preconditions.checkArgument(p.getIsDir(), "父分类不是分类");
            }
            taskCronDao().save(body);
            return;
        }
        boolean needSaveCron = false;
        if(body.getCronSetting() != null && StrUtil.isNotBlank(body.getId())){
            final ZtTaskCronEntity old = taskCronDao().getById(body.getId());
            if(old.getCronSetting() == null){
                needSaveCron = true;
            }else if(!StrUtil.equals(body.getCronSetting().toString(), old.getCronSetting().toString())){
                needSaveCron = true;
            }
        }
        taskCronDao().save(body);
        if(needSaveCron){
            taskCronDao().saveCron(body);
        }
        saveContent(body);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(@NotNull(message = "id不能为空") String id) {
        long subCount = taskCronDao().count(QueryBuilder.lambda(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getPid, id).build());
        Preconditions.assertTrue(subCount > 0, "存在子项不允许删除.");
        taskCronDao().removeById(id);
        deleteContent(id);
        return true;
    }

    /**
     * 获取任务内容
     * @param taskInfo
     * @return
     */
    protected abstract T taskContent(final ZtTaskCronEntity taskInfo);

    protected abstract T saveContent(final T t);

    protected abstract Boolean toggleEnabled(final String id, final Boolean enabled);

    protected abstract Boolean deleteContent(final String id);
}