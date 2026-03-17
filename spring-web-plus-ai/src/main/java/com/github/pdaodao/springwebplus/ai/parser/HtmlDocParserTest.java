package com.github.pdaodao.springwebplus.ai.parser;

import java.io.File;

/**
 * HtmlDocParser 测试
 */
public class HtmlDocParserTest {

    public static void main(String[] args) {
        // 测试解析HTML文档
        String filePath = "/Users/peng/Documents/dyna/dyna-meeting/dyna-meeting/asr/index.html";
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("文件不存在: " + filePath);
            return;
        }

        HtmlDocParser parser = new HtmlDocParser();
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
