package com.github.pdaodao.springwebplus.tool.task;

import com.github.pdaodao.springwebplus.tool.task.core.ArrayListTaskLoader;

import java.util.List;

/**
 * 任务信息加载器
 */
public interface TaskLoader {
    List<TaskInfo> load();

    void updateCronInfo(final TaskInfo taskInfo);

    static ArrayListTaskLoader ofList(){
        return new ArrayListTaskLoader();
    }
}
