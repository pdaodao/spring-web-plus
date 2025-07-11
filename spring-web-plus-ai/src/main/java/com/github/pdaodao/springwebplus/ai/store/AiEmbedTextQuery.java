package com.github.pdaodao.springwebplus.ai.store;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class AiEmbedTextQuery {
    /**
     * 命名空间 doc, table, api
     */
    private String namespace;

    /**
     * 团队id
     */
    private String teamId;

    /**
     * 知识库id
     */
    private String dbId;

    /**
     * 文档id
     */
    private String docId;

    /**
     * 文本内容
     */
    private String content;

    private Boolean enabled;

    /**
     * 向量
     */
    private float[] embedding;

    /**
     * 文档数
     */
    private Integer topK = 5;

    /**
     * 得分 相似度
     */
    private Double score = 0.6;

    public static AiEmbedTextQuery of(final String text){
        final AiEmbedTextQuery q = new AiEmbedTextQuery();
        q.setContent(text);
        return q;
    }


    public static List<Float> asList(float[] sp) {
        if(sp == null){
            return null;
        }
        final List<Float> list = new ArrayList(sp.length);
        for(final float f: sp){
            list.add(f);
        }
        return list;
    }
}
