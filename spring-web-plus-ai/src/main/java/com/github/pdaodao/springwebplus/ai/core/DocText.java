package com.github.pdaodao.springwebplus.ai.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 文档内容
 */
@Data
public class DocText {
    /**
     * id
     */
    private Long docId;

    /**
     * 命名空间
     */
    private String namespace;

    private String title;

    /**
     * 团队id
     */
    private Long teamId;


    @TableFieldIndex
    @Schema(description = "知识库id")
    private Long datasetId;

    @Schema(description = "文档内容")
    @Length(max = 3000, message = "文档内容长度超过3000限制")
    private String content;

    @Schema(description = "向量")
    private float[] embedding;

    @JsonIgnoreProperties
    private Double score;

    public String id(){
        return namespace+"-"+docId;
    }
}