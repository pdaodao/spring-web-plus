package com.github.pdaodao.springwebplus.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ZtTaskCronMapper extends BaseMapper<ZtTaskCronEntity> {

    List<ZtTaskLogEntity> logList(final TaskLogQuery query);

    void setExecutorRestartError(@Param("nodeId") final String nodeId);
}
