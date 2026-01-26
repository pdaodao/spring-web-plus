package com.github.pdaodao.springwebplus.task.service;

import com.github.pdaodao.springwebplus.task.dao.ZtTaskCronDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskCronEntity;
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
    private final ZtTaskCronDao taskInfoDao;

    @Override
    public List<CronTaskInfo> load() {
        final List<ZtTaskCronEntity> list = taskInfoDao.loadCron(DateTimeUtil.offsetSecond(DateTimeUtil.now(), 70));
        final List<CronTaskInfo> ret = new ArrayList<>();
        for(final ZtTaskCronEntity t: list){
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
    public Boolean setNext(String taskId, Long nextTime) {
        return taskInfoDao.setNext(taskId, nextTime);
    }
}
