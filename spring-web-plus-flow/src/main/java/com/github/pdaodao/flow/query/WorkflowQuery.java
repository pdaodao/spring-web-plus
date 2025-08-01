package com.github.pdaodao.flow.query;

import com.github.pdaodao.springwebplus.base.entity.WithTeam;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WorkflowQuery extends PageRequestParam implements WithTeam {
    @Schema(description = "团队id")
    private String teamId;

    @Schema(description = "是否发布")
    private Boolean published;
}
