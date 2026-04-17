package com.github.pdaodao.springwebplus.ai.parser;

import java.io.File;

/**
 * PdfDocParser 测试
 */
public class PdfDocParserTest {

    public static void main(String[] args) {
        // 测试解析PDF文档
        String filePath = "/Users/peng/Downloads/testtttt_translated.pdf";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return;
        }

        PdfDocParser parser = new PdfDocParser();
        ParseOption option = new ParseOption();
        option.setExtractImage(true);

        try {
            DocParseResult result = parser.parse(file, option);

            System.out.println("=== 解析结果 ===");
            System.out.println("标题: " + result.getTitle());
            System.out.println("文件类型: " + result.getDocType());
            System.out.println("解析时间: " + result.getParseTimeMs() + "ms");
            System.out.println("块数量: " + result.getChunks().size());
            System.out.println();

            // 统计各类型块数量
            int textCount = 0;
            int imageCount = 0;
            for (DocParseResult.Chunk chunk : result.getChunks()) {
                if ("text".equals(chunk.getType())) {
                    textCount++;
                } else if ("image".equals(chunk.getType())) {
                    imageCount++;
                }
            }
            System.out.println("文本块: " + textCount);
            System.out.println("图片块: " + imageCount);
            System.out.println();

            System.out.println("=== 内容块 ===");
            for (int i = 0; i < result.getChunks().size(); i++) {
                DocParseResult.Chunk chunk = result.getChunks().get(i);
                System.out.println("块" + i + " [" + chunk.getType() + "/" + chunk.getLayoutType() + "]:");

                // 打印内容
                if (chunk.getContent() != null) {
                    String content = chunk.getContent();
                    System.out.println("  内容: " + content.substring(0, Math.min(80, content.length())) + (content.length() > 80 ? "..." : ""));
                }

                // 打印base64长度（图片）
                if (chunk.getBase64() != null) {
                    System.out.println("  Base64长度: " + chunk.getBase64().length());
                }

                // 打印页码
                if (chunk.getPageNumber() != null) {
                    System.out.println("  页码: " + chunk.getPageNumber());
                }

                // 打印格式信息
                if (chunk.getPosition() != null) {
                    DocParseResult.Position pos = chunk.getPosition();
                    System.out.println("  [对齐: " + pos.getAlignment() + ", 首行缩进: " + pos.getFirstLineIndent() + "]");
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
