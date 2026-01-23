package com.github.pdaodao.springwebplus.task.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskInfoEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.mapper.ZtTaskInfoMapper;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import com.github.pdaodao.springwebplus.tool.task.CronUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Date;
import java.util.List;

@Component
public class ZtTaskInfoDao extends BaseDao<ZtTaskInfoMapper, ZtTaskInfoEntity> {
    public Boolean toggleCron(final String id, final Boolean cronEnabled){
        return update(Wrappers.lambdaUpdate(ZtTaskInfoEntity.class)
                .eq(ZtTaskInfoEntity::getId, id)
                .set(ZtTaskInfoEntity::getEnabled, cronEnabled));
    }

    public List<ZtTaskInfoEntity> loadCron(final Date nextTime){
        return list(QueryBuilder.lambda(ZtTaskInfoEntity.class)
                .eq(ZtTaskInfoEntity::getEnabled, true)
                .le(ZtTaskInfoEntity::getNextTime, nextTime.getTime()).build());
    }

    /**
     * 保存调度信息其他信息不保存
     * @param entity
     * @return
     */
    public Boolean saveCron(@RequestBody ZtTaskInfoEntity entity) throws Exception{
        final Date next = CronUtil.nextTime(entity.getCronSetting(), new Date());
        final Long nextTime = next != null ? next.getTime() : null;
        return update(Wrappers.lambdaUpdate(ZtTaskInfoEntity.class)
                .eq(ZtTaskInfoEntity::getId, entity.getId())
                .set(ZtTaskInfoEntity::getCronSetting, entity.getCronSetting())
                .set(ZtTaskInfoEntity::getNextTime, nextTime));
    }

    public Boolean setNext(final String taskId, final Long nextTime){
        return update(Wrappers.lambdaUpdate(ZtTaskInfoEntity.class)
                .eq(ZtTaskInfoEntity::getId, taskId)
                .set(ZtTaskInfoEntity::getNextTime, nextTime));
    }

    public List<ZtTaskLogEntity> logList(final TaskLogQuery query){
        return baseMapper.logList(query);
    }
}
