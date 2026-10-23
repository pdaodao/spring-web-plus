package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskMetricDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskMetricEntity;
import com.github.pdaodao.springwebplus.tool.task.util.TaskContext;
import com.github.pdaodao.springwebplus.tool.task.util.TaskContextHolder;

/**
 * 任务运行指标工具类
 */
public class TaskMetricUtil {
    public static ZtTaskMetricEntity save(final String tableName, final Long readCount, final Long writeCount){
        final TaskContext context = TaskContextHolder.get();
        String id = context == null || StrUtil.isBlank(context.getLogId()) ? IdUtil.snowIdString() : context.getLogId();
        final ZtTaskMetricEntity metricEntity = new ZtTaskMetricEntity();
        metricEntity.setId(id);
        metricEntity.setLogId(context.getLogId());
        metricEntity.setTableName(tableName);
        metricEntity.setReadCount(readCount);
        metricEntity.setWriteCount(writeCount);
        SpringUtil.getBean(ZtTaskMetricDao.class).save(metricEntity);
        return metricEntity;
    }

    public static ZtTaskMetricEntity save(final ZtTaskMetricEntity metricEntity){
        if(metricEntity == null){
            return null;
        }
        SpringUtil.getBean(ZtTaskMetricDao.class).save(metricEntity);
        return metricEntity;
    }
}
