package com.github.pdaodao.springwebplus.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskInfoEntity;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskLogEntity;
import com.github.pdaodao.springwebplus.task.pojo.TaskLogQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ZtTaskInfoMapper extends BaseMapper<ZtTaskInfoEntity> {

    List<ZtTaskLogEntity> logList(final TaskLogQuery query);
    
}
