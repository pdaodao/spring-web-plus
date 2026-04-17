package com.github.pdaodao.springwebplus.ai.parser;

import java.io.File;
import java.util.List;

/**
 * TxtDocParser + DocumentSplitter 测试
 */
public class TxtDocParserTest {

    public static void main(String[] args) {
        // 测试1: Parser解析
        System.out.println("=== 测试1: Parser解析 ===");
        testParser();

        // 测试2: Splitter分块
        System.out.println("\n=== 测试2: Splitter分块 ===");
        testSplitter();

        // 测试3: Parser + Splitter 完整流程
        System.out.println("\n=== 测试3: 完整流程 ===");
        testFullFlow();
    }

    /**
     * 测试Parser
     */
    private static void testParser() {
        TxtDocParser parser = new TxtDocParser();

        String testContent = "这是文档标题\n\n" +
                "第一段内容，包含一些文字。这是第一段的第二行。\n\n" +
                "第二段内容！这是第二段的另一行。\n\n" +
                "第三段内容？\n\n" +
                "第四段内容；第五段内容。";

        try {
            File tempFile = File.createTempFile("test_", ".txt");
            tempFile.deleteOnExit();
            java.nio.file.Files.write(tempFile.toPath(), testContent.getBytes("UTF-8"));

            DocParseResult result = parser.parse(tempFile, new ParseOption());

            System.out.println("解析结果:");
            System.out.println("  标题: " + result.getTitle());
            System.out.println("  块数量: " + result.getChunks().size());

            for (DocParseResult.Chunk chunk : result.getChunks()) {
                System.out.println("  - [" + chunk.getLayoutType() + "] " +
                        chunk.getContent().substring(0, Math.min(20, chunk.getContent().length())) + "...");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试Splitter
     */
    private static void testSplitter() {
        // 测试文本
        String text = "第一段内容。这是一段比较长的文本，包含多个句子。" +
                "这是第二句话。" +
                "这是第三句话！" +
                "第四句话？" +
                "第五句话；" +
                "第六句话。第七句话。第八句话。第九句话。第十句话。" +
                "第十一句话。第十二句话。第十三句话。第十四句话。第十五句话。" +
                "第十六句话。第十七句话。第十八句话。第十九句话。第二十句话。";

        // 测试不同分块大小
        int[] chunkSizes = {50, 100, 200};

        for (int size : chunkSizes) {
            System.out.println("\n--- 分块大小: " + size + " tokens ---");

            DocumentSplitter.SplitOption option = new DocumentSplitter.SplitOption();
            option.setChunkTokenNum(size);
            option.setDelimiter("\n。；！？");

            List<DocParseResult.Chunk> chunks = DocumentSplitter.split(text, option);

            System.out.println("分块数量: " + chunks.size());

            int totalTokens = 0;
            for (int i = 0; i < chunks.size(); i++) {
                DocParseResult.Chunk chunk = chunks.get(i);
                int tokens = TokenCounter.countTokens(chunk.getContent());
                totalTokens += tokens;
                System.out.println("  块" + i + ": " + tokens + " tokens, " +
                        chunk.getContent().substring(0, Math.min(30, chunk.getContent().length())) + "...");
            }
            System.out.println("  总token数: " + totalTokens);
        }
    }

    /**
     * 测试完整流程：Parser + Splitter
     */
    private static void testFullFlow() {
        TxtDocParser parser = new TxtDocParser();

        // 创建一个较长的测试文档
        StringBuilder sb = new StringBuilder();
        sb.append("文档标题\n\n");
        for (int i = 1; i <= 20; i++) {
            sb.append("第").append(i).append("段内容。这是第").append(i)
                    .append("段的第二句话。第三句话！第四句话？第五句话；第六句话。\n\n");
        }

        try {
            File tempFile = File.createTempFile("test_full_", ".txt");
            tempFile.deleteOnExit();
            java.nio.file.Files.write(tempFile.toPath(), sb.toString().getBytes("UTF-8"));

            // 1. Parser解析
            DocParseResult parseResult = parser.parse(tempFile, new ParseOption());
            System.out.println("Parser结果:");
            System.out.println("  段落数: " + parseResult.getChunks().size());

            // 2. Splitter分块
            DocumentSplitter.SplitOption splitOption = new DocumentSplitter.SplitOption();
            splitOption.setChunkTokenNum(128);
            splitOption.setDelimiter("\n。；！？");

            List<DocParseResult.Chunk> chunks = DocumentSplitter.split(parseResult.getChunks(), splitOption);

            System.out.println("\nSplitter结果:");
            System.out.println("  分块数: " + chunks.size());

            int totalTokens = 0;
            for (int i = 0; i < chunks.size(); i++) {
                DocParseResult.Chunk chunk = chunks.get(i);
                int tokens = TokenCounter.countTokens(chunk.getContent());
                totalTokens += tokens;
                System.out.println("  块" + i + ": " + tokens + " tokens, " +
                        chunk.getContent().substring(0, Math.min(40, chunk.getContent().length())) + "...");
            }
            System.out.println("  总token数: " + totalTokens);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
