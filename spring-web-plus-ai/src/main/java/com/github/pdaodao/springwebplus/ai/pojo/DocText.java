package com.github.pdaodao.springwebplus.ai.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

/**
 * 文档内容
 */
@Data
public class DocText {
    private String id;
    /**
     * 团队id
     */
    private String teamId;


    @Schema(description = "文档id")
    private String docId;


    @Schema(description = "知识库id")
    private String datasetId;

    @Schema(description = "文档内容")
//    @Length(max = 3000, message = "文档内容长度超过3000限制")
    private String content;

    @Schema(description = "向量")
    private List<Float> embedding;

    @JsonIgnoreProperties
    private Double score;
}
