package com.github.pdaodao.springwebplus.ai.core;

import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class DocTextQuery {
    /**
     * 团队id
     */
    private Long teamId;

    private Long docId;

    private String namespace;

    @TableFieldIndex
    @Schema(description = "知识库id")
    private Long datasetId;

    @Schema(description = "文档内容")
    @Length(max = 3000, message = "文档内容长度超过3000限制")
    private String content;

    @Schema(description = "向量检索")
    private float[] embedding;

    @Schema(description = "最小得分")
    private Float minScore = 0.5f;

    private Integer topK = 5;
}
