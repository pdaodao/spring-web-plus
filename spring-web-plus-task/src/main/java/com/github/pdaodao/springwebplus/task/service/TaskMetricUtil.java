package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.IdUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskMetricDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskMetricEntity;
import com.github.pdaodao.springwebplus.tool.task.LogUtil;
import com.github.pdaodao.springwebplus.tool.task.TaskLogContext;

/**
 * 任务运行指标工具类
 */
public class TaskMetricUtil {
    public static void save(final String tableName, final Long readCount, final Long writeCount){
        final TaskLogContext context = LogUtil.getContext();
        String id = context == null || StrUtil.isBlank(context.getLogId()) ? IdUtil.snowIdString() : context.getLogId();
        final ZtTaskMetricEntity metricEntity = new ZtTaskMetricEntity();
        metricEntity.setId(id);
        metricEntity.setTableName(tableName);
        metricEntity.setReadCount(readCount);
        metricEntity.setWriteCount(writeCount);
        SpringUtil.getBean(ZtTaskMetricDao.class).save(metricEntity);
    }
}
