package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.task.core.ArrayListTaskInfoLoader;

import java.util.List;

/**
 * 任务信息加载器
 */
public interface TaskInfoLoader {
    List<TaskInfo> load();

    void updateCronInfo(final TaskInfo taskInfo);

    static ArrayListTaskInfoLoader ofList(){
        return new ArrayListTaskInfoLoader();
    }
}
