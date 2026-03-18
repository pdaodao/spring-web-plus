package com.github.pdaodao.springwebplus.ai.store.lucene;

import cn.hutool.core.util.BooleanUtil;
import com.github.pdaodao.springwebplus.ai.AiVectorStore;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedText;
import com.github.pdaodao.springwebplus.ai.store.AiEmbedTextQuery;
import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.ik.IKAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Lucene 向量存储实现
 * 参考 Elasticsearch 实现，支持混合检索
 */
public class LuceneStore implements AiVectorStore {
    private static final Logger logger = LoggerFactory.getLogger(LuceneStore.class);
    private final Path indexPath;
    private final Analyzer analyzer;
    private final Directory directory;
    private final IndexWriter indexWriter;

    // 混合检索配置
    private final double vectorWeight = 0.5;
    private final double textWeight = 0.5;

    // 近实时读取器
    private volatile DirectoryReader directoryReader;

    // 内存缓存已删除的文档ID
    private final Set<String> deletedDocIds = ConcurrentHashMap.newKeySet();

    public LuceneStore(Path indexPath) throws IOException {
        this.indexPath = indexPath;
        // this.analyzer = new IKAnalyzer();
        this.analyzer = new CJKAnalyzer();
        // 确保索引目录存在
        if (indexPath != null && !Files.exists(indexPath)) {
            Files.createDirectories(indexPath);
        }
        // 初始化目录和写入器
        this.directory = indexPath != null ? FSDirectory.open(indexPath) : FSDirectory.open(Files.createTempDirectory("lucene"));
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setRAMBufferSizeMB(64.0);
        // 设置缓存 5000 个文档后刷新
        config.setMaxBufferedDocs(5000);
        // 使用分层合并策略
        TieredMergePolicy mergePolicy = new TieredMergePolicy();
        config.setMergePolicy(mergePolicy);
        this.indexWriter = new IndexWriter(directory, config);
        logger.info("LuceneStore initialized with indexPath: {}", indexPath);
    }

    /**
     * 刷新读取器 - 实现近实时索引
     */
    private void refreshReaderIfNeeded() {
        synchronized (this) {
            try {
                if (directoryReader != null) {
                    directoryReader.close();
                }
                directoryReader = DirectoryReader.open(directory);
            } catch (IOException e) {
                logger.warn("Failed to refresh reader: {}", e.getMessage());
            }
        }
    }

    /**
     * 使读取器失效
     */
    private void invalidateReader() {
        synchronized (this) {
            if (directoryReader != null) {
                try {
                    directoryReader.close();
                } catch (IOException e) {
                    logger.warn("Error closing reader: {}", e.getMessage());
                }
                directoryReader = null;
            }
        }
    }

    @Override
    public synchronized void save(List<AiEmbedText> documents, boolean isDelete) throws Exception {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        // 如果需要先删除
        if(BooleanUtil.isTrue(isDelete)){
            // 旧数据的查询条件
            final AiEmbedTextQuery query = new AiEmbedTextQuery();
            for(final AiEmbedText t: documents){
                query.addNamespace(t.getNamespace());
                query.setTeamId(t.getTeamId());
                query.addTopic(t.getTopic());
                query.addDocId(t.getDocId());
                query.addType(t.getType());
                query.addId(t.getId());
            }
            // 删除旧数据
            deleteByQuery(query);
        }
        final List<Document> list = new ArrayList<>();
        // 添加或更新文档
        for (final AiEmbedText text : documents) {
            list.add(LuceneUtil.toDoc(text));
        }
        indexWriter.addDocuments(list);
        // 提交更改
        commit();
    }

    /**
     * 提交更改 - 需要加锁以保证线程安全
     */
    private synchronized void commit() throws IOException {
        indexWriter.flush();
        indexWriter.commit();
        invalidateReader();
    }

    @Override
    public List<AiEmbedText> query(AiEmbedTextQuery query) throws Exception {
        if (query == null) {
            return Collections.emptyList();
        }
        refreshReaderIfNeeded();
        DirectoryReader reader = directoryReader;
        if (reader == null) {
            return Collections.emptyList();
        }
        final IndexSearcher searcher = new IndexSearcher(reader);

        int topK = query.getTopK() != null ? query.getTopK() : 5;
        double scoreThreshold = query.getScore() != null ? query.getScore() : 0.6;

        // 构建过滤条件
        Query filterQuery = LuceneUtil.buildFilter(query);

        // 如果有查询向量和内容，使用混合检索
        if (query.getEmbedding() != null && query.getEmbedding().length > 0
                && query.getContent() != null && !query.getContent().isEmpty()) {
            return hybridSearch(searcher, reader, query, filterQuery, topK, scoreThreshold);
        }

        // 如果有查询向量，使用向量搜索
        if (query.getEmbedding() != null && query.getEmbedding().length > 0) {
            return vectorSearch(searcher, reader, query, filterQuery, topK, scoreThreshold);
        }

        // 如果有查询内容但没有向量，使用文本搜索
        if (query.getContent() != null && !query.getContent().isEmpty()) {
            return textSearch(searcher, reader, query, filterQuery, topK, scoreThreshold);
        }

        // 否则使用过滤搜索
        return filterSearch(searcher, reader, query, topK);
    }


    /**
     * 混合检索 - 向量搜索 + 文本搜索
     */
    private List<AiEmbedText> hybridSearch(IndexSearcher searcher, DirectoryReader reader,
                                            AiEmbedTextQuery query, Query filterQuery,
                                            int topK, double scoreThreshold) throws Exception {
        // 1. 向量搜索 (KNN)
        Map<String, Double> vectorScores = new HashMap<>();
        float[] queryEmbedding = query.getEmbedding();
        KnnFloatVectorQuery knnQuery = new KnnFloatVectorQuery(LuceneUtil.EMBEDDING_FIELD, queryEmbedding, topK * 10);
        TopDocs vectorDocs = searcher.search(knnQuery, topK * 10);

        double maxVectorScore = 0.0;
        for (ScoreDoc scoreDoc : vectorDocs.scoreDocs) {
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            String docId = doc.get(LuceneUtil.ID_FIELD);
            vectorScores.put(docId, (double) scoreDoc.score);
            maxVectorScore = Math.max(maxVectorScore, scoreDoc.score);
        }

        // 归一化向量分数
        if (maxVectorScore > 0) {
            for (Map.Entry<String, Double> entry : vectorScores.entrySet()) {
                vectorScores.put(entry.getKey(), entry.getValue() / maxVectorScore);
            }
        }

        // 2. 文本搜索 (类似 Elasticsearch 的 match query)
        Map<String, Double> textScores = new HashMap<>();
        QueryParser parser = new QueryParser(LuceneUtil.CONTENT_FIELD, analyzer);
        Query textQuery = parser.parse(QueryParser.escape(query.getContent().trim()));

        // 结合过滤条件
        BooleanQuery.Builder boolBuilder = new BooleanQuery.Builder();
        boolBuilder.add(textQuery, BooleanClause.Occur.MUST);
        if (filterQuery != null && ((BooleanQuery) filterQuery).clauses().size() > 0) {
            boolBuilder.add(filterQuery, BooleanClause.Occur.MUST);
        }

        TopDocs textDocs = searcher.search(boolBuilder.build(), topK * 10);
        double maxTextScore = 0.0;
        for (ScoreDoc scoreDoc : textDocs.scoreDocs) {
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            String docId = doc.get(LuceneUtil.ID_FIELD);
            textScores.put(docId, (double) scoreDoc.score);
            maxTextScore = Math.max(maxTextScore, scoreDoc.score);
        }

        // 归一化文本分数
        if (maxTextScore > 0) {
            for (Map.Entry<String, Double> entry : textScores.entrySet()) {
                textScores.put(entry.getKey(), entry.getValue() / maxTextScore);
            }
        }

        // 3. 合并分数
        Set<String> allDocIds = new HashSet<>();
        allDocIds.addAll(vectorScores.keySet());
        allDocIds.addAll(textScores.keySet());

        List<AiEmbedText> results = new ArrayList<>();
        Set<String> matchedDocIds = new HashSet<>();

        // 计算混合分数
        Map<String, Double> hybridScores = new HashMap<>();
        for (String docId : allDocIds) {
            Double vScore = vectorScores.getOrDefault(docId, 0.0);
            Double tScore = textScores.getOrDefault(docId, 0.0);
            double hybridScore = vectorWeight * vScore + textWeight * tScore;
            hybridScores.put(docId, hybridScore);
        }

        // 按分数排序
        List<Map.Entry<String, Double>> sortedEntries = new ArrayList<>(hybridScores.entrySet());
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Double> entry : sortedEntries) {
            if (results.size() >= topK) {
                break;
            }

            String docId = entry.getKey();
            double score = entry.getValue();

            if (score < scoreThreshold) {
                continue;
            }

            if (matchedDocIds.contains(docId)) {
                continue;
            }
            matchedDocIds.add(docId);

            // 获取完整文档
            TermQuery termQuery = new TermQuery(new Term(LuceneUtil.ID_FIELD, docId));
            TopDocs docs = searcher.search(termQuery, 1);
            if (docs.scoreDocs.length > 0) {
                Document doc = searcher.storedFields().document(docs.scoreDocs[0].doc);
                AiEmbedText embedText = LuceneUtil.createAiEmbedText(doc, score);
                results.add(embedText);
            }
        }

        logger.info("Hybrid search found {} results", results.size());
        return results;
    }

    /**
     * 向量搜索
     */
    private List<AiEmbedText> vectorSearch(IndexSearcher searcher, DirectoryReader reader,
                                            AiEmbedTextQuery query, Query filterQuery,
                                            int topK, double scoreThreshold) throws IOException {
        // 使用 KNN 向量搜索
        float[] queryEmbedding = query.getEmbedding();
        KnnFloatVectorQuery knnQuery = new KnnFloatVectorQuery(LuceneUtil.EMBEDDING_FIELD, queryEmbedding, topK * 10);

        // 结合过滤条件
        BooleanQuery.Builder boolBuilder = new BooleanQuery.Builder();
        boolBuilder.add(knnQuery, BooleanClause.Occur.MUST);
        if (filterQuery != null && ((BooleanQuery) filterQuery).clauses().size() > 0) {
            boolBuilder.add(filterQuery, BooleanClause.Occur.MUST);
        }

        TopDocs topDocs = searcher.search(boolBuilder.build(), topK * 2);

        List<AiEmbedText> results = new ArrayList<>();
        Set<String> matchedDocIds = new HashSet<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            String docId = doc.get(LuceneUtil.ID_FIELD);

            // 跳过已删除的文档
            if (deletedDocIds.contains(docId)) {
                continue;
            }

            if (matchedDocIds.contains(docId)) {
                continue;
            }
            matchedDocIds.add(docId);

            double score = scoreDoc.score;
            if (score < scoreThreshold) {
                continue;
            }

            AiEmbedText embedText = LuceneUtil.createAiEmbedText(doc, score);
            results.add(embedText);

            if (results.size() >= topK) {
                break;
            }
        }

        logger.info("Vector search found {} results", results.size());
        return results;
    }

    /**
     * 文本搜索 - 类似 Elasticsearch 的 match query
     */
    private List<AiEmbedText> textSearch(IndexSearcher searcher, DirectoryReader reader,
                                         AiEmbedTextQuery query, Query filterQuery,
                                         int topK, double scoreThreshold) throws Exception {
        // 使用 QueryParser 对 content 字段进行搜索
        QueryParser parser = new QueryParser(LuceneUtil.CONTENT_FIELD, analyzer);
        Query textQuery = parser.parse(QueryParser.escape(query.getContent().trim()));

        // 结合过滤条件
        BooleanQuery.Builder boolBuilder = new BooleanQuery.Builder();
        boolBuilder.add(textQuery, BooleanClause.Occur.MUST);
        if (filterQuery != null && ((BooleanQuery) filterQuery).clauses().size() > 0) {
            boolBuilder.add(filterQuery, BooleanClause.Occur.MUST);
        }

        TopDocs topDocs = searcher.search(boolBuilder.build(), topK * 2);

        List<AiEmbedText> results = new ArrayList<>();
        Set<String> matchedDocIds = new HashSet<>();

        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            if (results.size() >= topK) {
                break;
            }

            Document doc = searcher.storedFields().document(scoreDoc.doc);
            String docId = doc.get(LuceneUtil.ID_FIELD);

            // 跳过已删除的文档
            if (deletedDocIds.contains(docId)) {
                continue;
            }

            if (matchedDocIds.contains(docId)) {
                continue;
            }
            matchedDocIds.add(docId);

            double score = scoreDoc.score;
            if (score < scoreThreshold) {
                continue;
            }

            AiEmbedText embedText = LuceneUtil.createAiEmbedText(doc, score);
            results.add(embedText);
        }

        logger.info("Text search found {} results", results.size());
        return results;
    }

    /**
     * 过滤搜索
     */
    private List<AiEmbedText> filterSearch(IndexSearcher searcher, DirectoryReader reader,
                                            AiEmbedTextQuery query, int topK) throws IOException {
        Query filterQuery = LuceneUtil.buildFilter(query);

        if (filterQuery == null || ((BooleanQuery) filterQuery).clauses().size() == 0) {
            return Collections.emptyList();
        }

        TopDocs topDocs = searcher.search(filterQuery, topK);

        List<AiEmbedText> results = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.storedFields().document(scoreDoc.doc);
            String docId = doc.get(LuceneUtil.ID_FIELD);

            if (deletedDocIds.contains(docId)) {
                continue;
            }

            AiEmbedText embedText = LuceneUtil.createAiEmbedText(doc, 1.0);
            results.add(embedText);
        }

        return results;
    }



    @Override
    public synchronized Long deleteByQuery(AiEmbedTextQuery query) throws Exception {
        refreshReaderIfNeeded();
        final IndexSearcher searcher = new IndexSearcher(directoryReader);
        final Query filterQuery = LuceneUtil.buildFilter(query);
        if (filterQuery == null) {
            return 0L;
        }
        // 删除文档
        long deleteSize = indexWriter.deleteDocuments(filterQuery);
        commit();
        logger.info("Deleted {} documents by query", deleteSize);
        return deleteSize;
    }

    @Override
    public long count() throws Exception {
        refreshReaderIfNeeded();
        DirectoryReader reader = directoryReader;
        if (reader == null) {
            return 0;
        }
        return reader.numDocs();
    }

    /**
     * 关闭资源
     */
    public void close() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
        if (directoryReader != null) {
            directoryReader.close();
        }
        if (directory != null) {
            directory.close();
        }
        if (analyzer != null) {
            analyzer.close();
        }
    }
}
