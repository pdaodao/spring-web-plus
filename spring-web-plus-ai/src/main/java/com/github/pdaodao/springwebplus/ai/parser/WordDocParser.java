package com.github.pdaodao.springwebplus.ai.parser;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Word文档解析器
 * 对应RAGFlow: deepdoc/parser (无对应实现，本Java实现基于Apache POI)
 *
 * 功能：
 * - 提取段落文本（支持标题样式识别）
 * - 提取表格内容
 * - 提取嵌入式图片（可选，需要调用OCR识别图片文字）
 *
 * 注意：长文本分块由外部Splitter组件负责
 */
@Slf4j
public class WordDocParser extends AbstractDocParser {

    /**
     * 标题样式前缀
     */
    private static final String HEADING_PREFIX = "Heading";

    /**
     * 标题样式正则
     */
    private static final Pattern HEADING_PATTERN = Pattern.compile("Heading\\s*(\\d+)");

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();

        option = option != null ? option : new ParseOption();

        try (InputStream is = java.nio.file.Files.newInputStream(file.toPath());
             XWPFDocument document = new XWPFDocument(is)) {

            List<DocParseResult.Chunk> chunks = new ArrayList<>();
            String title = null;
            int positionIndex = 0;

            // 按文档原始顺序处理（段落、表格、图片混合）
            List<IBodyElement> bodyElements = document.getBodyElements();
            for (IBodyElement element : bodyElements) {
                if (element instanceof XWPFParagraph) {
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                    String text = paragraph.getText();

                    // 提取段落中的图片（按位置插入）
                    if (option.isExtractImage()) {
                        extractImagesFromParagraph(paragraph, chunks, positionIndex);
                    }

                    if (text == null || text.trim().isEmpty()) {
                        continue;
                    }

                    // 检测是否为标题
                    String layoutType = detectLayoutType(paragraph);

                    // 提取标题（第一个标题）
                    if (title == null && "title".equals(layoutType)) {
                        title = text.trim();
                    }

                    // 提取段落格式信息
                    DocParseResult.Position position = extractParagraphPosition(paragraph);

                    chunks.add(DocParseResult.Chunk.builder()
                            .id(generateChunkId())
                            .type("text")
                            .content(text)
                            .layoutType(layoutType)
                            .positionIndex(positionIndex++)
                            .position(position)
                            .build());

                } else if (element instanceof XWPFTable) {
                    XWPFTable table = (XWPFTable) element;
                    String tableContent = extractTableText(table);
                    String tableHtml = extractTableHtml(table);

                    if (tableContent != null && !tableContent.trim().isEmpty()) {
                        chunks.add(DocParseResult.Chunk.builder()
                                .id(generateChunkId())
                                .type("table")
                                .content(tableContent)
                                .htmlContent(tableHtml)
                                .layoutType("table")
                                .positionIndex(positionIndex++)
                                .build());
                    }
                }
            }

            // 4. OCR识别图片文字（可选）
            if (option.isExtractImage()) {
                // TODO: 调用OcrService识别图片中的文字
                // for each image chunk -> OcrService.recognize() -> add as text chunk
            }

            return DocParseResult.builder()
                    .title(title)
                    .docType(DocTypeEnum.DOCX)
                    .chunks(chunks)
                    .fileName(file.getName())
                    .parseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (IOException e) {
            log.error("解析Word文档失败: {}", file.getName(), e);
            throw new RuntimeException("解析Word文档失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检测段落布局类型
     * 优先级：样式ID(数字) > 样式名称 > 大纲级别
     */
    private String detectLayoutType(XWPFParagraph paragraph) {
        // 1. 检查样式ID (纯数字如 "1","2","3" 对应标题1-9)
        String styleId = paragraph.getStyleID();
        if (styleId != null && !styleId.isEmpty()) {
            // 纯数字样式ID是Word内部对标题样式的映射
            if (styleId.matches("\\d+")) {
                return "title";
            }
        }

        // 2. 检查样式名称（支持中英文）
        String style = paragraph.getStyle();
        if (style != null && !style.isEmpty()) {
            String s = style.toLowerCase();
            // 匹配 "标题 1", "标题1", "Heading 1", "Heading1", "Title" 等
            if (s.matches(".*(标题|heading|title)\\s*\\d*.*")) {
                return "title";
            }
        }

        // 3. 检查大纲级别（Word内部标题级别）
        try {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP ctp = paragraph.getCTP();
            if (ctp != null && ctp.getPPr() != null && ctp.getPPr().getOutlineLvl() != null) {
                return "title";
            }
        } catch (Exception ignored) {
            // 忽略异常
        }

        return "text";
    }

    /**
     * 提取表格文本内容
     */
    private String extractTableText(XWPFTable table) {
        StringBuilder sb = new StringBuilder();
        List<XWPFTableRow> rows = table.getRows();

        for (int i = 0; i < rows.size(); i++) {
            XWPFTableRow row = rows.get(i);
            List<XWPFTableCell> cells = row.getTableCells();

            for (int j = 0; j < cells.size(); j++) {
                XWPFTableCell cell = cells.get(j);
                String cellText = cell.getText();
                if (cellText != null) {
                    sb.append(cellText.trim());
                }
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
     * 提取表格HTML
     */
    private String extractTableHtml(XWPFTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");

        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            sb.append("<tr>");
            List<XWPFTableCell> cells = row.getTableCells();

            for (XWPFTableCell cell : cells) {
                sb.append("<td>");
                sb.append(escapeHtml(cell.getText()));
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    /**
     * 提取段落格式信息（对齐、首行缩进）
     */
    private DocParseResult.Position extractParagraphPosition(XWPFParagraph paragraph) {
        DocParseResult.Position.PositionBuilder builder = DocParseResult.Position.builder();

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP ctp = paragraph.getCTP();
        if (ctp != null && ctp.getPPr() != null) {
            org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr pPr = ctp.getPPr();

            // 对齐
            if (pPr.getJc() != null && pPr.getJc().getVal() != null) {
                String align = pPr.getJc().getVal().toString().toUpperCase();
                if (align.contains("CENTER")) {
                    builder.alignment("CENTER");
                } else if (align.contains("RIGHT") || align.contains("END")) {
                    builder.alignment("RIGHT");
                } else if (align.contains("JUSTIFY") || align.contains("BOTH")) {
                    builder.alignment("JUSTIFIED");
                } else {
                    builder.alignment("LEFT");
                }
            }

            // 首行缩进 - EMU转磅(1磅=20EMU)
            if (pPr.getInd() != null) {
                org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd ind = pPr.getInd();
                try {
                    Object firstLine = ind.getFirstLine();
                    if (firstLine != null) {
                        builder.firstLineIndent(Double.parseDouble(firstLine.toString()) / 20.0);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return builder.build();
    }

    /**
     * 从段落中提取图片（嵌入在Run中的图片）
     */
    /**
     * 从单个段落中提取图片
     */
    private void extractImagesFromParagraph(XWPFParagraph paragraph,
                                           List<DocParseResult.Chunk> chunks, int startIndex) {
        int positionIndex = startIndex;

        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null) return;

        for (XWPFRun run : runs) {
            List<XWPFPicture> pictures = run.getEmbeddedPictures();
            for (XWPFPicture pic : pictures) {
                XWPFPictureData pictureData = pic.getPictureData();
                if (pictureData == null) continue;

                byte[] imageData = pictureData.getData();
                String base64 = Base64.getEncoder().encodeToString(imageData);
                String format = getImageFormat(pictureData.getPictureTypeEnum());

                chunks.add(DocParseResult.Chunk.builder()
                        .id(generateChunkId())
                        .type("image")
                        .content(base64)
                        .imageFormat(format)
                        .layoutType("image")
                        .positionIndex(positionIndex++)
                        .build());
            }
        }
    }

    /**
     * 从段落列表中提取图片（已废弃，用extractImagesFromParagraph代替）
     */
    private void extractImagesFromParagraphs(List<IBodyElement> bodyElements,
                                              List<DocParseResult.Chunk> chunks, int startIndex) {
        int positionIndex = startIndex;

        for (IBodyElement element : bodyElements) {
            if (!(element instanceof XWPFParagraph)) {
                continue;
            }

            XWPFParagraph paragraph = (XWPFParagraph) element;
            List<XWPFRun> runs = paragraph.getRuns();
            if (runs == null) continue;

            for (XWPFRun run : runs) {
                List<XWPFPicture> pictures = run.getEmbeddedPictures();
                for (XWPFPicture pic : pictures) {
                    XWPFPictureData pictureData = pic.getPictureData();
                    if (pictureData == null) continue;

                    byte[] imageData = pictureData.getData();
                    String base64 = Base64.getEncoder().encodeToString(imageData);
                    String format = getImageFormat(pictureData.getPictureTypeEnum());

                    chunks.add(DocParseResult.Chunk.builder()
                            .id(generateChunkId())
                            .type("image")
                            .content(base64)
                            .imageFormat(format)
                            .layoutType("image")
                            .positionIndex(positionIndex++)
                            .build());
                }
            }
        }

        System.out.println("DEBUG - 从段落提取图片数量: " + (positionIndex - startIndex));
    }

    /**
     * 获取图片格式
     */
    private String getImageFormat(PictureType pictureType) {
        if (pictureType == null) {
            return "png";
        }
        switch (pictureType) {
            case JPEG:
                return "jpeg";
            case GIF:
                return "gif";
            case BMP:
                return "bmp";
            case TIFF:
                return "tiff";
            case WMF:
                return "wmf";
            case EMF:
                return "emf";
            default:
                return "png";
        }
    }

    @Override
    public DocTypeEnum getSupportType() {
        return DocTypeEnum.DOCX;
    }
}
