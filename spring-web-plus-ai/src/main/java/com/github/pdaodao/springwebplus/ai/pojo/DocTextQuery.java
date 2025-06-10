package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
public class DocTextQuery {
    /**
     * 团队id
     */
    private String teamId;

//    @TableFieldIndex
    @Schema(description = "文档id")
    private String docId;

//    @TableFieldIndex
    @Schema(description = "知识库id")
    private String datasetId;

    @Schema(description = "文档内容")
//    @Length(max = 3000, message = "文档内容长度超过3000限制")
    private String content;

    @Schema(description = "向量检索")
    private List<Float> embedding;

    @Schema(description = "最小得分")
    private Double minScore = 0.5;

    private Integer topK = 5;
}
