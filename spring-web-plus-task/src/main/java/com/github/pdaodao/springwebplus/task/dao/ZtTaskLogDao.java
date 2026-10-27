package com.github.pdaodao.springwebplus.task.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.mapper.ZtTaskLogMapper;
import com.github.pdaodao.springwebplus.tool.task.base.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class ZtTaskLogDao extends BaseDao<ZtTaskLogMapper, ZtTaskLogEntity> {
    public boolean setRunningErrorByNodeId(final String nodeId){
        return update(Wrappers.lambdaUpdate(ZtTaskLogEntity.class)
                .eq(ZtTaskLogEntity::getNodeId, nodeId)
                .eq(ZtTaskLogEntity::getTaskStatus, TaskStatus.running)
                .set(ZtTaskLogEntity::getTaskStatus, TaskStatus.cancel)
                .set(ZtTaskLogEntity::getError, "执行节点重启"));
    }

    public boolean setDataRows(final String id, final Long dataRows){
        return update(Wrappers.lambdaUpdate(ZtTaskLogEntity.class)
                .eq(ZtTaskLogEntity::getTaskId, id)
                .set(ZtTaskLogEntity::getDataRows, dataRows));
    }
}
