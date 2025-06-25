package com.github.pdaodao.springwebplus.ai.store;

import lombok.Data;

import java.util.Map;

@Data
public class AiEmbedText {
    private String id;

    /**
     * 命名空间 doc, table, api
     */
    private String namespace;

    /**
     * 团队id
     */
    private Long teamId;

    /**
     * 知识库id
     */
    private Long datasetId;

    /**
     * 文档id
     */
    private Long docId;

    /**
     * 文本块id
     */
    private Long textId;

    /**
     * 表名， 字段名 ...
     */
    private String name;

    // 内容
    private String content;

    // 向量
    private float[] embedding;

    private Double score;

    private Map<String, Object> meta;

    public static AiEmbedText of(final String text){
        final AiEmbedText a = new AiEmbedText();
        a.setContent(text);
        return a;
    }

    @Override
    public String toString() {
        return content;
    }
}
