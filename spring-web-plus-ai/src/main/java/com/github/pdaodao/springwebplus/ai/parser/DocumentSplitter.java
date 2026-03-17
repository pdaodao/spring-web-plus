package com.github.pdaodao.springwebplus.ai.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文档分块器
 * 对应RAGFlow: rag/nlp/naive_merge()
 *
 * 功能：
 * - 按标点符号分割段落
 * - 按token数限制块大小
 * - 优先在完整句子后切割，不直接分开单词
 */
public class DocumentSplitter {

    /**
     * 默认分隔符（句子结束标点）
     * 中文：。；！？……——
     * 英文：.!?;
     * 日文：。！？———
     * 韩文：.;!?;
     */
    public static final String DEFAULT_DELIMITER = "\n。；！？……——.!?;";

    /**
     * 最小段落token数（小于此值会合并到下一段）
     */
    private static final int MIN_SECTION_TOKENS = 8;

    @Data
    public static class SplitOption {
        /**
         * 每块token数
         */
        private int chunkTokenNum = 512;

        /**
         * 分隔符（支持自定义，如：`\n!?.。；！？`）
         */
        private String delimiter = DEFAULT_DELIMITER;

        /**
         * 重叠百分比 [0, 1)
         */
        private double overlappedPercent = 0;

        public SplitOption() {}

        public SplitOption(int chunkTokenNum, String delimiter) {
            this.chunkTokenNum = chunkTokenNum;
            this.delimiter = delimiter;
        }
    }

    /**
     * 分块入口方法
     *
     * @param sections 段落列表（每个元素是DocParseResult.Chunk）
     * @param option   分块选项
     * @return 分块后的Chunk列表
     */
    public static List<DocParseResult.Chunk> split(List<DocParseResult.Chunk> sections, SplitOption option) {
        if (sections == null || sections.isEmpty()) {
            return new ArrayList<>();
        }

        option = option != null ? option : new SplitOption();

        // 1. 先按分隔符细分段落
        List<SubSection> subSections = new ArrayList<>();
        for (DocParseResult.Chunk section : sections) {
            List<SubSection> splits = splitByDelimiter(section.getContent(), option.getDelimiter());
            for (SubSection sub : splits) {
                sub.parentLayoutType = section.getLayoutType();
                sub.pageNumber = section.getPageNumber();
                subSections.add(sub);
            }
        }

        // 2. 按token数合并
        return mergeIntoChunks(subSections, option);
    }

    /**
     * 直接对文本分块（简化版）
     *
     * @param text   文本内容
     * @param option 分块选项
     * @return 分块后的Chunk列表
     */
    public static List<DocParseResult.Chunk> split(String text, SplitOption option) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        option = option != null ? option : new SplitOption();

        // 创建一个临时Chunk
        DocParseResult.Chunk tempChunk = DocParseResult.Chunk.builder()
                .content(text)
                .layoutType("text")
                .build();

        return split(List.of(tempChunk), option);
    }

    /**
     * 按分隔符分割段落
     * 对应RAGFlow: naive_merge中的分割逻辑
     */
    private static List<SubSection> splitByDelimiter(String text, String delimiter) {
        List<SubSection> result = new ArrayList<>();

        if (text == null || text.isEmpty()) {
            return result;
        }

        // 按分隔符分割
        String regex = "[" + Pattern.quote(delimiter) + "]";
        String[] parts = text.split(regex, -1);

        for (String part : parts) {
            if (part == null || part.isEmpty() || part.trim().isEmpty()) {
                continue;
            }
            // 保留该部分以及它后面的分隔符
            result.add(new SubSection(part, ""));
        }

        return result;
    }

    /**
     * 构建分隔符正则表达式
     */
    private static String buildDelimiterRegex(String delimiter) {
        StringBuilder regex = new StringBuilder("(");
        for (int i = 0; i < delimiter.length(); i++) {
            char c = delimiter.charAt(i);
            regex.append("\\").append(c);
        }
        regex.append(")");
        return regex.toString();
    }

    /**
     * 将子段落按token数合并为块
     * 对应RAGFlow: naive_merge() 核心逻辑
     */
    private static List<DocParseResult.Chunk> mergeIntoChunks(List<SubSection> subSections, SplitOption option) {
        List<DocParseResult.Chunk> chunks = new ArrayList<>();
        List<String> currentChunkTexts = new ArrayList<>();
        List<Integer> currentChunkTokens = new ArrayList<>();

        double threshold = option.getChunkTokenNum() * (100 - option.getOverlappedPercent() * 100) / 100.0;

        for (SubSection sub : subSections) {
            int subTokens = TokenCounter.countTokens(sub.content);

            // 跳过太短的空内容
            if (sub.content.trim().isEmpty()) {
                continue;
            }

            // 计算当前块的token总数
            int currentTotalTokens = currentChunkTokens.stream().mapToInt(Integer::intValue).sum();

            // RAGFlow逻辑：先追加，再检查
            // 如果当前块为空，直接添加
            // 否则追加内容，然后检查是否超阈值
            if (currentChunkTokens.isEmpty()) {
                currentChunkTexts.add(sub.content);
                currentChunkTokens.add(subTokens);
            } else {
                // 追加到当前块（先不加换行，模拟RAGFlow的 +=）
                if (subTokens >= MIN_SECTION_TOKENS) {
                    // 较大段落，加换行
                    currentChunkTexts.add("\n" + sub.content);
                } else {
                    // 小段落，直接合并
                    currentChunkTexts.add(sub.content);
                }
                currentChunkTokens.add(subTokens);

                // 追加后再检查是否超阈值（和RAGFlow一致）
                currentTotalTokens = currentChunkTokens.stream().mapToInt(Integer::intValue).sum();
                if (currentTotalTokens > threshold) {
                    // 超阈值了，需要新建块
                    // 保存当前块（包含超出的部分）
                    chunks.add(createChunk(currentChunkTexts, sub.parentLayoutType, chunks.size()));
                    currentChunkTexts.clear();
                    currentChunkTokens.clear();
                }
            }
        }

        // 处理最后一个块
        if (!currentChunkTexts.isEmpty()) {
            chunks.add(createChunk(currentChunkTexts, "text", chunks.size()));
        }

        return chunks;
    }

    /**
     * 创建Chunk对象
     */
    private static DocParseResult.Chunk createChunk(List<String> texts, String layoutType, int positionIndex) {
        String content = String.join("", texts);
        // 清理开头多余的换行
        if (content.startsWith("\n")) {
            content = content.substring(1);
        }

        return DocParseResult.Chunk.builder()
                .id(generateChunkId())
                .type("text")
                .content(content)
                .layoutType(layoutType)
                .positionIndex(positionIndex)
                .build();
    }

    /**
     * 生成chunk ID
     */
    private static String generateChunkId() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 内部类：子段落
     */
    private static class SubSection {
        String content;
        String delimiter;  // 结尾的分隔符
        String parentLayoutType;
        Integer pageNumber;

        SubSection(String content, String delimiter) {
            this.content = content;
            this.delimiter = delimiter;
        }
    }
}
