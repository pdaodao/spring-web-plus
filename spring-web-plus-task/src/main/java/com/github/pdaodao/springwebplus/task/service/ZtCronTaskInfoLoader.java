package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.task.dao.ZtTaskInfoDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskInfoEntity;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.CronTaskLoader;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ZtCronTaskInfoLoader implements CronTaskLoader {
    private final ZtTaskInfoDao taskInfoDao;

    @Override
    public List<CronTaskInfo> load() {
        final List<ZtTaskInfoEntity> list = taskInfoDao.loadCron(DateTimeUtil.offsetSecond(DateTimeUtil.now(), 70));
        final List<CronTaskInfo> ret = new ArrayList<>();
        for(final ZtTaskInfoEntity t: list){
            final CronTaskInfo cronTaskInfo = new CronTaskInfo();
            cronTaskInfo.setTaskId(t.getId());
            cronTaskInfo.setTaskType(t.getTaskType());
            cronTaskInfo.setNextTime(t.getNextTime());
            cronTaskInfo.setCronSetting(t.getCronSetting());
            ret.add(cronTaskInfo);
        }
        return ret;
    }

    @Override
    public Boolean setNext(Long taskId, Long nextTime) {
        return taskInfoDao.setNext(taskId, nextTime);
    }
}
