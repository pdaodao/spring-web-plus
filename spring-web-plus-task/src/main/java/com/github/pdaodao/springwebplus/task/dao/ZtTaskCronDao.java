package com.github.pdaodao.springwebplus.task.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.mapper.ZtTaskCronMapper;
import com.github.pdaodao.springwebplus.task.pojo.TaskCronQuery;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.tool.task.CronUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.management.Query;
import java.util.Date;
import java.util.List;

@Component
public class ZtTaskCronDao extends BaseDao<ZtTaskCronMapper, ZtTaskCronEntity> {
    public Boolean toggleCron(final String id, final Boolean cronEnabled){
        return update(Wrappers.lambdaUpdate(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getId, id)
                .set(ZtTaskCronEntity::getEnabled, cronEnabled));
    }

    public List<ZtTaskCronEntity> loadCron(final Date nextTime){
        return list(QueryBuilder.lambda(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getEnabled, true)
                .le(ZtTaskCronEntity::getNextTime, nextTime.getTime()).build());
    }

    public List<ZtTaskCronEntity> list(final TaskCronQuery query){
        return list(QueryBuilder.lambda(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getTeamId, query.getTeamId())
                .eq(ZtTaskCronEntity::getNamespace, query.getNamespace())
                .like(query.getQ(), ZtTaskCronEntity::getTitle).build());
    }

    /**
     * 保存调度信息其他信息不保存
     * @param entity
     * @return
     */
    public Boolean saveCron(@RequestBody ZtTaskCronEntity entity) throws Exception{
        final Date now = DateTimeUtil.now();
        Date lastTime = entity.getCronSetting().getBeginTime();
        if(lastTime == null || now.after(lastTime)){
            lastTime = now;
        }
        final Date next = CronUtil.nextTime(entity.getCronSetting(), lastTime);
        final Long nextTime = next != null ? next.getTime() : null;
        entity.setNextTime(nextTime);
        return update(Wrappers.lambdaUpdate(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getId, entity.getId())
                .set(ZtTaskCronEntity::getCronSetting, JsonUtil.toJsonString(entity.getCronSetting()))
                .set(ZtTaskCronEntity::getNextTime, nextTime));
    }

    public Boolean setNext(final String taskId, final Long nextTime){
        return update(Wrappers.lambdaUpdate(ZtTaskCronEntity.class)
                .eq(ZtTaskCronEntity::getId, taskId)
                .set(ZtTaskCronEntity::getNextTime, nextTime));
    }

    public List<ZtTaskLogEntity> logList(final TaskLogQuery query){
        return baseMapper.logList(query);
    }

    public void setExecutorRestartError(final String nodeId){

    }
}