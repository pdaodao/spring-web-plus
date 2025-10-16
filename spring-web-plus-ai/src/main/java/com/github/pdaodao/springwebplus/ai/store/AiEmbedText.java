package com.github.pdaodao.springwebplus.ai.store;

import lombok.Data;

import java.util.Map;

@Data
public class AiEmbedText {
    private String id;

    /**
     * 命名空间 大类 用作区分不同地方的数据
     */
    private String namespace;

    /**
     * 团队id
     */
    private String teamId;

    /**
     * 知识库主题id
     */
    private String topic;

    /**
     * 文档id
     */
    private String docId;

    /**
     * 文本块id
     */
    private String textId;

    /**
     * 内部类型
     */
    private String type;

    /**
     * 表名， 字段名 ...
     */
    private String name;

    // 中文名称
    private String title;

    // 内容
    private String content;

    // 向量
    private float[] embedding;

    private Double score;

    private Map<String, Object> meta;

    public static AiEmbedText of(final String text) {
        final AiEmbedText a = new AiEmbedText();
        a.setContent(text);
        return a;
    }

    @Override
    public String toString() {
        return content;
    }
}