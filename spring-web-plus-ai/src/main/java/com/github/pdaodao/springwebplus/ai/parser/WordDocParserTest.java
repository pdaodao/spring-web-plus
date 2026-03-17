package com.github.pdaodao.springwebplus.ai.parser;

import java.io.File;
import java.util.List;

/**
 * WordDocParser 测试
 */
public class WordDocParserTest {

    public static void main(String[] args) {
        // 测试解析Word文档
        String filePath = "/Users/peng/Downloads/testtttt_translated.docx";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return;
        }

        WordDocParser parser = new WordDocParser();
        ParseOption option = new ParseOption();
        option.setExtractImage(false);  // 先不提取图片

        try {
            DocParseResult result = parser.parse(file, option);

            System.out.println("=== 解析结果 ===");
            System.out.println("标题: " + result.getTitle());
            System.out.println("文件类型: " + result.getDocType());
            System.out.println("解析时间: " + result.getParseTimeMs() + "ms");
            System.out.println("块数量: " + result.getChunks().size());
            System.out.println();

            System.out.println("=== 内容块 ===");
            for (int i = 0; i < result.getChunks().size(); i++) {
                DocParseResult.Chunk chunk = result.getChunks().get(i);
                System.out.println("块" + i + " [" + chunk.getType() + "/" + chunk.getLayoutType() + "]:");
                String content = chunk.getContent();
                if (content != null) {
                    System.out.println("  " + content.substring(0, Math.min(80, content.length())) + (content.length() > 80 ? "..." : ""));
                } else if (chunk.getType().equals("image")) {
                    System.out.println("  [图片数据]");
                }

                // 打印格式信息
                if (chunk.getPosition() != null) {
                    DocParseResult.Position pos = chunk.getPosition();
                    System.out.println("  [对齐: " + pos.getAlignment() +
                            ", 首行缩进: " + pos.getFirstLineIndent() + "]");
                }
            }

            // 测试转换为Markdown
            System.out.println("\n=== Markdown输出 ===");
            String markdown = DocumentStructureRestorer.toMarkdown(result);
            System.out.println(markdown);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
