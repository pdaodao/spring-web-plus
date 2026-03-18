package com.github.pdaodao.springwebplus.ai.store.lucene;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import com.github.pdaodao.springwebplus.ai.store.elasticsearch.EsQueryUtil;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class LuceneUtil {
    // Lucene 字段名常量
    public static final String ID_FIELD = "id";
    public static final String CONTENT_FIELD = "content";
    public static final String EMBEDDING_FIELD = "embedding";
    public static final String NAMESPACE_FIELD = "namespace";
    public static final String TEAM_ID_FIELD = "teamId";
    public static final String TYPE_FIELD = "type";
    public static final String TOPIC_FIELD = "topic";
    public static final String DOC_ID_FIELD = "docId";
    public static final String TITLE_FIELD = "title";
    public static final String NAME_FIELD = "name";


    public static Document toDoc(final AiEmbedText text){
        if(text == null){
            return null;
        }
        final Document doc = new Document();
        addStringIfNotNull(doc, ID_FIELD, text.getId());
        addStringIfNotNull(doc, NAMESPACE_FIELD, text.getNamespace());
        addStringIfNotNull(doc, TOPIC_FIELD, text.getTopic());
        addStringIfNotNull(doc, TYPE_FIELD, text.getType());
        addStringIfNotNull(doc, DOC_ID_FIELD, text.getDocId());
        addStringIfNotNull(doc, NAME_FIELD, text.getName());
        addStringIfNotNull(doc, TITLE_FIELD, text.getTitle());
        addKeywordIfNotNull(doc, CONTENT_FIELD, text.getContent());
        if(ArrayUtil.isNotEmpty(text.getEmbedding())){
            doc.add(new KnnFloatVectorField(EMBEDDING_FIELD, text.getEmbedding(), VectorSimilarityFunction.COSINE));
        }
        // 元数据字段
        if(MapUtil.isNotEmpty(text.getMeta())){
            for(Map.Entry<String, Object> entry: text.getMeta().entrySet()){
                if(ObjectUtil.isNull(entry.getValue())){
                    continue;
                }
                addStringIfNotNull(doc, "meta_"+entry.getKey(), ObjectUtil.toString(entry.getValue()));
            }
        }
        return doc;
    }


    private static void addStringIfNotNull(final Document doc, final String field, final String text){
        if(ObjectUtil.isNull(text)){
            return;
        }
        doc.add(new StringField(field, text, Field.Store.YES));
    }

    private static void addKeywordIfNotNull(final Document doc, final String field, final String text){
        if(ObjectUtil.isNull(text)){
            return;
        }
        doc.add(new TextField(field, text, Field.Store.YES));
    }


    /**
     * 过滤条件
     *
     * @param query
     * @return
     */
    public static Query buildFilter(final AiEmbedTextQuery query) {
        if (query == null) {
            return null;
        }
        final BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        // 团队id
        if (StrUtil.isNotBlank(query.getTeamId())) {
            boolQuery.add(new TermQuery(new Term(TEAM_ID_FIELD, query.getTeamId())), BooleanClause.Occur.MUST);
        }
        // 命名空间
        if (CollUtil.isNotEmpty(query.getNamespaces())) {
            final Query namespace = equalOrIn(NAMESPACE_FIELD, query.getNamespaces());
            if(namespace != null){
                boolQuery.add(namespace, BooleanClause.Occur.MUST);
            }
        }
        // type
        if (CollUtil.isNotEmpty(query.getTypes())) {
            final Query namespace = equalOrIn(TYPE_FIELD, query.getTypes());
            if(namespace != null){
                boolQuery.add(namespace, BooleanClause.Occur.MUST);
            }
        }
        // 主题
        if (CollUtil.isNotEmpty(query.getTopics())) {
            final Query namespace = equalOrIn(TOPIC_FIELD, query.getTypes());
            if(namespace != null){
                boolQuery.add(namespace, BooleanClause.Occur.MUST);
            }
        }
        // 文档id
        if (CollUtil.isNotEmpty(query.getDocIds())) {
            final Query namespace = equalOrIn(DOC_ID_FIELD, query.getTypes());
            if(namespace != null){
                boolQuery.add(namespace, BooleanClause.Occur.MUST);
            }
        }
        // 主键
        if (CollUtil.isNotEmpty(query.getIds())) {
            final Query namespace = equalOrIn(ID_FIELD, query.getTypes());
            if(namespace != null){
                boolQuery.add(namespace, BooleanClause.Occur.MUST);
            }
        }
        return boolQuery.build();
    }


    private static Query equalOrIn(final String field, final Collection<String> values){
        if(CollUtil.isEmpty(values)){
            return null;
        }
        if(CollUtil.size(values) == 1){
            return new TermQuery(new Term(field, values.iterator().next()));
        }
        // 多个值使用 BooleanQuery SHOULD
        final BooleanQuery.Builder builder = new BooleanQuery.Builder();
        for (String value : values) {
            builder.add(new TermQuery(new Term(field, value)), BooleanClause.Occur.SHOULD);
        }
        return builder.build();
    }


    /**
     * 从 Lucene Document 创建 AiEmbedText
     */
    public static AiEmbedText createAiEmbedText(Document doc, double score) {
        AiEmbedText embedText = new AiEmbedText();
        embedText.setId(doc.get(ID_FIELD));
        embedText.setContent(doc.get(CONTENT_FIELD));
        embedText.setNamespace(doc.get(NAMESPACE_FIELD));
        embedText.setTeamId(doc.get(TEAM_ID_FIELD));
        embedText.setType(doc.get(TYPE_FIELD));
        embedText.setTopic(doc.get(TOPIC_FIELD));
        embedText.setDocId(doc.get(DOC_ID_FIELD));
        embedText.setTitle(doc.get(TITLE_FIELD));
        embedText.setName(doc.get(NAME_FIELD));
        embedText.setScore(score);

        // 获取存储的向量
        IndexableField embeddingField = doc.getField(EMBEDDING_FIELD);
        if (embeddingField != null && embeddingField.binaryValue() != null) {
            byte[] bytes = embeddingField.binaryValue().bytes;
            float[] embedding = new float[bytes.length / 4];
            for (int i = 0; i < embedding.length; i++) {
                int bits = ((bytes[i * 4] & 0xFF) << 24) |
                        ((bytes[i * 4 + 1] & 0xFF) << 16) |
                        ((bytes[i * 4 + 2] & 0xFF) << 8) |
                        (bytes[i * 4 + 3] & 0xFF);
                embedding[i] = Float.intBitsToFloat(bits);
            }
            embedText.setEmbedding(embedding);
        }

        return embedText;
    }
}
