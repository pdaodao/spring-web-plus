package com.github.pdaodao.springwebplus.ai.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Data
@Schema(description = "向量查询")
public class AiEmbedTextQuery {
    @Schema(description = "命名空间")
    private Set<String> namespaces = new HashSet<>();

    @Schema(description = "团队-租户id")
    private String teamId;

    @Schema(description = "类型")
    private Set<String> types  = new HashSet<>();

    @Schema(description = "主题")
    private Set<String> topics  = new HashSet<>();

    @Schema(description = "文档id")
    private Set<String> docIds  = new HashSet<>();

    @Schema(description = "主键")
    private Set<String> ids  = new HashSet<>();

    @Schema(description = "检索内容")
    private String content;

    @Schema(description = "检索向量")
    private float[] embedding;

    @Schema(description = "topK文档数")
    private Integer topK = 5;

    @Schema(description = "相似度得分")
    private Double score = 0.6;


    public static AiEmbedTextQuery of(final String text) {
        final AiEmbedTextQuery q = new AiEmbedTextQuery();
        q.setContent(text);
        return q;
    }

    public void addNamespace(final String namespace){
        if(StrUtil.isBlank(namespace)){
            return;
        }
        if(namespaces == null){
            namespaces = new HashSet<>();
        }
        namespaces.add(namespace);
    }

    public void addType(final String type){
        if(StrUtil.isBlank(type)){
            return;
        }
        if(types == null){
            types = new HashSet<>();
        }
        types.add(type);
    }

    public void addId(final String id){
        if(StrUtil.isBlank(id)){
            return;
        }
        if(ids == null){
            ids = new HashSet<>();
        }
        ids.add(id);
    }

    public void addTopic(final String topic){
        if(StrUtil.isBlank(topic)){
            return;
        }
        if(topics == null){
            topics = new HashSet<>();
        }
        topics.add(topic);
    }

    public void addTopics(final Collection<String> topicIds){
        if(CollUtil.isEmpty(topicIds)){
            return;
        }
        if(topics == null){
            topics = new HashSet<>();
        }
        topics.addAll(topicIds);
    }


    public void addDocId(final String docId){
        if(StrUtil.isBlank(docId)){
            return;
        }
        if(docIds == null){
            docIds = new HashSet<>();
        }
        docIds.add(docId);
    }

    public static List<Float> asList(float[] sp) {
        if (sp == null) {
            return null;
        }
        final List<Float> list = new ArrayList(sp.length);
        for (final float f : sp) {
            list.add(f);
        }
        return list;
    }
}
