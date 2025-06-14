package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.task.core.ArrayListTaskLoader;

import java.util.List;

/**
 * 任务信息加载器
 */
public interface CronTaskLoader {
    List<CronTaskInfo> load();

    Boolean setNext(final Long taskId, final Long nextTime);


    static ArrayListTaskLoader ofList(){
        return new ArrayListTaskLoader();
    }
}
