package com.github.pdaodao.springwebplus.tool.task.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.tool.task.CronUtil;
import com.github.pdaodao.springwebplus.tool.task.CronTaskInfo;
import com.github.pdaodao.springwebplus.tool.task.CronTaskLoader;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArrayListTaskLoader implements CronTaskLoader {
    private final List<CronTaskInfo> list = new ArrayList<>();

    public synchronized void clear(){
        list.clear();
    }

    public synchronized void add(final CronTaskInfo task) throws Exception{
        if(task == null){
            return;
        }
        if(task.getNextTime() == null){
            final Date next = CronUtil.nextTime(task.getCronSetting(), new Date());
            Preconditions.checkNotNull(next, "next-time is null");
            task.setNextTime(next.getTime());
        }
        list.add(task);
    }

    public synchronized void addAll(final List<CronTaskInfo> tasks){
        if(tasks == null){
            return;
        }
        list.addAll(tasks);
    }

    @Override
    public synchronized List<CronTaskInfo> load() {
        if(CollUtil.isEmpty(list)){
            return ListUtil.empty();
        }
        return ListUtil.toList(list);
    }

    @Override
    public Boolean setNext(String taskId, Long nextTime) {
        return true;
    }
}
