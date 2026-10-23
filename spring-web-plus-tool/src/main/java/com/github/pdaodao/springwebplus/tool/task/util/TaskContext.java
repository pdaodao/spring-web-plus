package com.github.pdaodao.springwebplus.tool.task.util;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TaskContext {
    private String teamId;
    private String taskId;
    private String logId;
    private LocalDateTime startTime;
    private Map<String, Object> map = new ConcurrentHashMap<>();

    public static TaskContext of(final String taskId, final String logId, final LocalDateTime startTime){
        final TaskContext ct = new TaskContext();
        ct.setTaskId(taskId);
        ct.setLogId(logId);
        ct.setStartTime(startTime);
        return ct;
    }

    public void put(final String key, final Object v){
        if(StrUtil.isBlank(key) || ObjectUtil.isNull(v)){
            return;
        }
        map.put(key, v);
    }

    public Object get(final String key){
        if(StrUtil.isBlank(key)){
            return null;
        }
        return map.get(key);
    }
}