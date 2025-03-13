package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.auth.SysLogListener;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.entity.SysLog;
import com.github.pdaodao.springwebplus.mapper.SysLogMapper;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SysLogDao extends BaseDao<SysLogMapper, SysLog> implements SysLogListener {

    @Async
    @Override
    public void onSave(com.github.pdaodao.springwebplus.base.pojo.SysLog sysLog) {
        if(sysLog == null){
            return;
        }
        final SysLog entity = new SysLog();
        BeanUtils.copyProperties(sysLog, entity);
        entity.setDescription(sysLog.getOperation());
        entity.setOperationType(sysLog.getLogType() != null ? sysLog.getLogType().name() : null);
        save(entity);
    }
}
