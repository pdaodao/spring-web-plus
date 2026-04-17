package com.github.pdaodao.springwebplus.ai.query;

import com.github.pdaodao.springwebplus.base.pojo.WithTimeQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AiQuestionAnswerQuery extends WithTimeQuery {
    @Schema(hidden = true)
    private String teamId;

    @Schema(description = "父分类id")
    private String pid;

    @Schema(description = "是否是分类")
    private Boolean isDir;
}