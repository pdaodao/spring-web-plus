package com.github.pdaodao.springwebplus.ai.parser;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档结构还原器
 * 用于将解析后的内容按原始顺序输出
 */
public class DocumentStructureRestorer {

    /**
     * 按原始顺序获取所有内容块
     */
    public static List<DocParseResult.Chunk> restore(DocParseResult result) {
        if (result == null || result.getChunks() == null) {
            return List.of();
        }

        // 按positionIndex排序
        return result.getChunks().stream()
                .sorted(Comparator.comparingInt(c -> c.getPositionIndex() != null ? c.getPositionIndex() : 0))
                .collect(Collectors.toList());
    }

    /**
     * 转换为Markdown格式
     */
    public static String toMarkdown(DocParseResult result) {
        List<DocParseResult.Chunk> chunks = restore(result);
        StringBuilder sb = new StringBuilder();

        for (DocParseResult.Chunk chunk : chunks) {
            switch (chunk.getType()) {
                case "text":
                    if ("title".equals(chunk.getLayoutType())) {
                        sb.append("## ").append(chunk.getContent());
                    } else {
                        sb.append(chunk.getContent());
                    }
                    break;
                case "table":
                    sb.append("\n").append(chunk.getContent()).append("\n");
                    break;
                case "image":
                    if (chunk.getContent() != null && chunk.getImageFormat() != null) {
                        sb.append("![image](data:image/").append(chunk.getImageFormat())
                                .append(";base64,").append(chunk.getContent()).append(")");
                    }
                    break;
            }
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }

    /**
     * 转换为HTML格式
     */
    public static String toHtml(DocParseResult result) {
        List<DocParseResult.Chunk> chunks = restore(result);
        StringBuilder sb = new StringBuilder();

        for (DocParseResult.Chunk chunk : chunks) {
            switch (chunk.getType()) {
                case "text":
                    if ("title".equals(chunk.getLayoutType())) {
                        sb.append("<h2>").append(escapeHtml(chunk.getContent())).append("</h2>");
                    } else {
                        sb.append("<p>").append(escapeHtml(chunk.getContent())).append("</p>");
                    }
                    break;
                case "table":
                    if (chunk.getHtmlContent() != null) {
                        sb.append(chunk.getHtmlContent());
                    }
                    break;
                case "image":
                    if (chunk.getContent() != null && chunk.getImageFormat() != null) {
                        sb.append("<img src=\"data:image/").append(chunk.getImageFormat())
                                .append(";base64,").append(chunk.getContent()).append("\"/>");
                    }
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * 转换为纯文本
     */
    public static String toPlainText(DocParseResult result) {
        List<DocParseResult.Chunk> chunks = restore(result);
        StringBuilder sb = new StringBuilder();

        for (DocParseResult.Chunk chunk : chunks) {
            if ("text".equals(chunk.getType()) || "table".equals(chunk.getType())) {
                if (sb.length() > 0) {
                    sb.append("\n\n");
                }
                sb.append(chunk.getContent());
            }
        }

        return sb.toString();
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
