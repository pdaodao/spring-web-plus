package com.github.pdaodao.springwebplus.ai.parser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * HTML文档解析器
 * 对应RAGFlow: deepdoc/parser (无对应实现，本Java实现基于Jsoup)
 *
 * 功能：
 * - 提取标题（h1-h6）
 * - 提取段落文本（p, div等）
 * - 提取表格
 * - 提取图片
 *
 * 注意：长文本分块由外部Splitter组件负责
 */
@Slf4j
public class HtmlDocParser extends AbstractDocParser {

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();

        option = option != null ? option : new ParseOption();

        try {
            // 读取文件内容
            String htmlContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            return parseHtml(htmlContent, file.getName(), option, startTime);
        } catch (IOException e) {
            log.error("解析HTML文件失败: {}", file.getName(), e);
            throw new RuntimeException("解析HTML文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析HTML字符串
     */
    public DocParseResult parseHtml(String htmlContent, String fileName, ParseOption option, long startTime) {
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        }

        option = option != null ? option : new ParseOption();

        Document doc = Jsoup.parse(htmlContent);

        List<DocParseResult.Chunk> chunks = new ArrayList<>();
        String title = null;
        int positionIndex = 0;

        // 1. 提取标题（title标签）
        Element titleElement = doc.selectFirst("title");
        if (titleElement != null) {
            title = titleElement.text();
        }

        // 2. 提取h1-h6标题
        Elements headings = doc.select("h1, h2, h3, h4, h5, h6");
        for (Element heading : headings) {
            String text = heading.text();
            if (text == null || text.trim().isEmpty()) {
                continue;
            }

            String layoutType = "title";

            // 第一个标题作为文档标题
            if (title == null) {
                title = text.trim();
            }

            // 提取段落格式信息（从style中提取）
            DocParseResult.Position position = extractPosition(heading);

            chunks.add(DocParseResult.Chunk.builder()
                    .id(generateChunkId())
                    .type("text")
                    .content(text.trim())
                    .layoutType(layoutType)
                    .positionIndex(positionIndex++)
                    .position(position)
                    .build());
        }

        // 3. 提取段落（p, div, span, li, td, th等标签的文本）
        // 按文档顺序遍历body下的所有元素（提取文本）
        extractTextFromElements(doc.body(), chunks, option);
        positionIndex = chunks.size();

        // 4. 提取表格
        Elements tables = doc.select("table");
        for (Element table : tables) {
            String tableContent = extractTableText(table);
            String tableHtml = table.html();

            if (tableContent != null && !tableContent.trim().isEmpty()) {
                chunks.add(DocParseResult.Chunk.builder()
                        .id(generateChunkId())
                        .type("table")
                        .content(tableContent.trim())
                        .htmlContent(tableHtml)
                        .layoutType("table")
                        .positionIndex(positionIndex++)
                        .build());
            }
        }

        // 5. 提取图片（可选）
        if (option.isExtractImage()) {
            Elements images = doc.select("img");
            for (Element img : images) {
                String src = img.attr("src");
                if (src == null || src.isEmpty()) {
                    continue;
                }

                // 处理base64图片
                if (src.startsWith("data:image")) {
                    // base64图片
                    String base64 = extractBase64FromDataUrl(src);
                    String format = extractImageFormat(src);

                    if (base64 != null) {
                        chunks.add(DocParseResult.Chunk.builder()
                                .id(generateChunkId())
                                .type("image")
                                .base64(base64)
                                .imageFormat(format)
                                .layoutType("image")
                                .positionIndex(positionIndex++)
                                .build());
                    }
                } else if (src.startsWith("http://") || src.startsWith("https://")) {
                    // 网络图片（暂不处理）
                    log.debug("跳过网络图片: {}", src);
                }
                // 本地相对路径图片需要读取文件，暂不处理
            }
        }

        return DocParseResult.builder()
                .title(title)
                .docType(DocTypeEnum.HTML)
                .chunks(chunks)
                .fileName(fileName)
                .parseTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 从元素中提取位置信息（对齐方式）
     */
    private DocParseResult.Position extractPosition(Element element) {
        String style = element.attr("style");
        if (style == null || style.isEmpty()) {
            return null;
        }

        DocParseResult.Position.PositionBuilder builder = DocParseResult.Position.builder();

        // 解析text-align
        if (style.contains("text-align:")) {
            if (style.contains("center")) {
                builder.alignment("CENTER");
            } else if (style.contains("right")) {
                builder.alignment("RIGHT");
            } else if (style.contains("justify") || style.contains("justify")) {
                builder.alignment("JUSTIFIED");
            } else {
                builder.alignment("LEFT");
            }
        }

        return builder.build();
    }

    /**
     * 递归提取文本元素
     */
    private void extractTextFromElements(Element element, List<DocParseResult.Chunk> chunks, ParseOption option) {
        // 跳过脚本和样式
        if (element.tagName().equals("script") || element.tagName().equals("style")
                || element.tagName().equals("meta") || element.tagName().equals("link")) {
            return;
        }

        // 如果是标题标签，跳过（已在前面处理）
        if (element.tagName().matches("h[1-6]")) {
            return;
        }

        // 如果是表格，跳过（单独处理）
        if (element.tagName().equals("table")) {
            return;
        }

        // 如果是图片标签，跳过
        if (element.tagName().equals("img")) {
            return;
        }

        // 获取纯文本内容
        String text = element.text();

        // 检查元素是否有子元素是标题/表格/图片
        boolean hasHeadingChild = !element.select("h1, h2, h3, h4, h5, h6").isEmpty();
        boolean hasTableChild = !element.select("table").isEmpty();
        boolean hasImageChild = !element.select("img").isEmpty();

        // 如果没有标题/表格/图片子元素，且有文本内容，则提取
        if (!hasHeadingChild && !hasTableChild && !hasImageChild && text != null && !text.trim().isEmpty()) {
            // 过滤过短的段落
            if (text.trim().length() >= option.getMinParagraphLength()) {
                DocParseResult.Position position = extractPosition(element);

                chunks.add(DocParseResult.Chunk.builder()
                        .id(generateChunkId())
                        .type("text")
                        .content(text.trim())
                        .layoutType("text")
                        .positionIndex(chunks.size())
                        .position(position)
                        .build());
            }
        }

        // 递归处理子元素
        for (Element child : element.children()) {
            extractTextFromElements(child, chunks, option);
        }
    }

    /**
     * 提取表格文本内容
     */
    private String extractTableText(Element table) {
        StringBuilder sb = new StringBuilder();
        Elements rows = table.select("tr");

        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cells = row.select("th, td");

            for (int j = 0; j < cells.size(); j++) {
                sb.append(cells.get(j).text());
                if (j < cells.size() - 1) {
                    sb.append("\t");
                }
            }
            if (i < rows.size() - 1) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * 从data URL中提取base64数据
     */
    private String extractBase64FromDataUrl(String dataUrl) {
        if (dataUrl == null || !dataUrl.contains(",")) {
            return null;
        }
        return dataUrl.substring(dataUrl.indexOf(",") + 1);
    }

    /**
     * 从data URL中提取图片格式
     */
    private String extractImageFormat(String dataUrl) {
        if (dataUrl == null || !dataUrl.contains(":")) {
            return "png";
        }
        String mimeType = dataUrl.substring(dataUrl.indexOf(":") + 1, dataUrl.indexOf(";"));
        if (mimeType.contains("jpeg") || mimeType.contains("jpg")) {
            return "jpeg";
        } else if (mimeType.contains("png")) {
            return "png";
        } else if (mimeType.contains("gif")) {
            return "gif";
        } else if (mimeType.contains("bmp")) {
            return "bmp";
        }
        return "png";
    }

    @Override
    public DocTypeEnum getSupportType() {
        return DocTypeEnum.HTML;
    }
}
