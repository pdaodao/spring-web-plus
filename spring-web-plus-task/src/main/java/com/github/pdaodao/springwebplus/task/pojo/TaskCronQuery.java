package com.github.pdaodao.springwebplus.task.pojo;

import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import lombok.Data;

@Data
public class TaskCronQuery extends PageRequestParam {
    private String teamId;

    private String namespace;
}
