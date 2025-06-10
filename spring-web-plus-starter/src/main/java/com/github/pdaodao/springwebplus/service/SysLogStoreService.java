package com.github.pdaodao.springwebplus.service;

import com.github.pdaodao.springwebplus.base.auth.SysLogListener;
import com.github.pdaodao.springwebplus.base.pojo.LogType;
import com.github.pdaodao.springwebplus.dao.SysLogDao;
import com.github.pdaodao.springwebplus.dao.SysLoginLogDao;
import com.github.pdaodao.springwebplus.entity.SysLog;
import com.github.pdaodao.springwebplus.entity.SysLoginLog;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SysLogStoreService implements SysLogListener {
    private final SysLogDao logDao;
    private final SysLoginLogDao loginDao;
    @Async
    @Override
    public void onSave(final com.github.pdaodao.springwebplus.base.pojo.SysLog sysLog) {
        if(sysLog == null){
            return;
        }
        if(LogType.LOGIN == sysLog.getLogType() || LogType.LOGOUT == sysLog.getLogType()){
            final SysLoginLog loginEntity = new SysLoginLog();
            BeanUtils.copyProperties(sysLog, loginEntity);
            loginEntity.setDescription(sysLog.getOperation());
            loginEntity.setOperationType(sysLog.getLogType() != null ? sysLog.getLogType().name() : null);
            loginDao.save(loginEntity);
            return;
        }
        final SysLog entity = new SysLog();
        BeanUtils.copyProperties(sysLog, entity);
        entity.setDescription(sysLog.getOperation());
        entity.setOperationType(sysLog.getLogType() != null ? sysLog.getLogType().name() : null);
        logDao.save(entity);
    }
}
