package com.github.pdaodao.springwebplus.ai.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * PDF文档解析器
 * 对应RAGFlow: deepdoc/parser/pdf_parser.py
 *
 * 基于Apache PDFBox实现生产可用的PDF解析器
 * 支持外部算法服务（OCR）进行增强
 *
 * 功能：
 * - 提取文本（按页面和段落）
 * - 提取图片（嵌入式图片）
 * - 可选：OCR识别图片中的文字
 */
@Slf4j
public class PdfDocParser extends AbstractDocParser {

    /**
     * 支持的图片格式
     */
    private static final java.util.List<String> SUPPORTED_IMAGE_FORMATS = java.util.List.of(
            "png", "jpg", "jpeg", "gif", "bmp"
    );

    // ==================== 外部服务（可选） ====================

    /**
     * OCR服务（可选，用于识别图片中的文字）
     */
    private OcrService ocrService;

    // ==================== 解析状态 ====================

    /**
     * 当前是否检测为英文文档
     */
    private boolean isEnglish = false;

    // ==================== 构造函数 ====================

    /**
     * 默认构造函数
     */
    public PdfDocParser() {
    }

    /**
     * 构造函数（注入OCR服务）
     */
    public PdfDocParser(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    // ==================== 服务注入 ====================

    public void setOcrService(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    public OcrService getOcrService() {
        return ocrService;
    }

    // ==================== 解析入口 ====================

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();

        option = option != null ? option : new ParseOption();

        // 检查文件格式
        String extension = getFileExtension(file.getName()).toLowerCase();
        if (!"pdf".equals(extension)) {
            throw new RuntimeException("不支持的文档格式: " + extension);
        }

        try {
            return parsePdf(file, option, startTime);
        } catch (Exception e) {
            log.error("解析PDF文件失败: {}", file.getName(), e);
            throw new RuntimeException("解析PDF文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析PDF
     */
    private DocParseResult parsePdf(File file, ParseOption option, long startTime) {
        String fileName = file.getName();

        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
            java.util.List<DocParseResult.Chunk> chunks = new java.util.ArrayList<>();
            String title = null;
            int positionIndex = 0;

            int pageCount = document.getNumberOfPages();

            // 检测文档语言
            detectLanguage(document);

            // 逐页解析
            for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                // 提取文本
                java.util.List<String> pageParagraphs = extractPageText(document, pageNum);

                // 处理文本块
                for (String para : pageParagraphs) {
                    if (para == null || para.trim().isEmpty()) {
                        continue;
                    }

                    // 跳过噪声
                    if (isNoise(para.trim())) {
                        continue;
                    }

                    String layoutType = detectLayoutType(para.trim());

                    // 第一个标题作为文档标题
                    if (title == null && "title".equals(layoutType)) {
                        title = para.trim();
                    }

                    chunks.add(DocParseResult.Chunk.builder()
                            .id(generateChunkId())
                            .type("text")
                            .content(para.trim())
                            .layoutType(layoutType)
                            .pageNumber(pageNum)
                            .positionIndex(positionIndex++)
                            .build());
                }

                // 提取图片（如果启用）
                if (option.isExtractImage()) {
                    PDPage page = document.getPage(pageNum - 1);
                    java.util.List<DocParseResult.Chunk> imageChunks = extractImages(page, pageNum, positionIndex);
                    chunks.addAll(imageChunks);
                    positionIndex += imageChunks.size();
                }
            }

            // 添加标题块（只有真正检测到标题时才添加）
            if (!chunks.isEmpty() && title != null) {
                // 检查是否已经有标题块
                boolean hasTitle = chunks.stream().anyMatch(c -> "title".equals(c.getLayoutType()));
                if (!hasTitle) {
                    chunks.add(0, DocParseResult.Chunk.builder()
                            .id(generateChunkId())
                            .type("text")
                            .content(title)
                            .layoutType("title")
                            .pageNumber(1)
                            .positionIndex(0)
                            .build());
                    // 重新编号
                    for (int i = 1; i < chunks.size(); i++) {
                        chunks.get(i).setPositionIndex(i);
                    }
                }
            }

            return DocParseResult.builder()
                    .title(title)
                    .docType(DocTypeEnum.PDF)
                    .chunks(chunks)
                    .fileName(fileName)
                    .parseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (Exception e) {
            log.error("解析PDF失败: {}", fileName, e);
            throw new RuntimeException("解析PDF文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 提取单页文本，返回段落列表
     */
    private java.util.List<String> extractPageText(PDDocument document, int pageNum) {
        java.util.List<String> paragraphs = new java.util.ArrayList<>();

        try {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNum);
            stripper.setEndPage(pageNum);
            String text = stripper.getText(document);

            if (text != null && !text.isEmpty()) {
                // 按空行分割为段落
                String[] lines = text.split("\n");
                java.util.List<String> paraLines = new java.util.ArrayList<>();

                for (String line : lines) {
                    if (line == null) {
                        continue;
                    }

                    line = line.trim();

                    if (line.isEmpty()) {
                        // 空行表示段落结束
                        if (!paraLines.isEmpty()) {
                            paragraphs.add(java.lang.String.join("\n", paraLines));
                            paraLines.clear();
                        }
                    } else {
                        paraLines.add(line);
                    }
                }

                // 处理最后一个段落
                if (!paraLines.isEmpty()) {
                    paragraphs.add(java.lang.String.join("\n", paraLines));
                }
            }
        } catch (Exception e) {
            log.warn("提取页面{}文本失败: {}", pageNum, e.getMessage());
        }

        return paragraphs;
    }

    /**
     * 检测文档语言
     */
    private void detectLanguage(PDDocument document) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(1);
            stripper.setEndPage(Math.min(5, document.getNumberOfPages()));
            String text = stripper.getText(document);

            if (text != null) {
                int englishCount = 0;
                int chineseCount = 0;
                for (char c : text.toCharArray()) {
                    if (c >= '\u4e00' && c <= '\u9fff') {
                        chineseCount++;
                    } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        englishCount++;
                    }
                }
                isEnglish = englishCount > chineseCount * 2;
            }
        } catch (Exception e) {
            log.warn("检测文档语言失败: {}", e.getMessage());
        }
    }

    /**
     * 检测布局类型
     */
    private String detectLayoutType(String text) {
        if (text == null || text.isEmpty()) {
            return "text";
        }

        // 中文标题模式
        if (text.matches("^第[一二三四五六七八九十百]+[章条节]")) {
            return "title";
        }
        if (text.matches("^(摘要|Abstract|目录|前言|参考文献|附录|致谢)$")) {
            return "title";
        }

        // 英文标题模式
        if (text.matches("^(Chapter|Section|Abstract|Contents|Introduction|References|Appendix|Acknowledgments)\\s*.*")) {
            return "title";
        }

        // 数字标题
        if (text.matches("^[0-9]+[.、].*") && text.length() < 80) {
            return "title";
        }

        // 短行且没有句号结尾，可能是标题
        if (text.length() < 60 && !text.endsWith(".") && !text.endsWith("。")) {
            return "title";
        }

        return "text";
    }

    /**
     * 检测是否为噪声
     */
    private boolean isNoise(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }

        text = text.trim();

        // 纯数字
        if (text.matches("^[0-9]+$")) {
            return true;
        }

        // 特殊符号
        if (text.matches("^[\\-\\*\\._]+$")) {
            return true;
        }

        // 网址
        if (text.matches("^https?://.*")) {
            return true;
        }

        return false;
    }

    /**
     * 从页面提取图片
     */
    private java.util.List<DocParseResult.Chunk> extractImages(PDPage page, int pageNum, int startIndex) {
        java.util.List<DocParseResult.Chunk> imageChunks = new java.util.ArrayList<>();

        try {
            PDResources resources = page.getResources();
            if (resources == null) {
                return imageChunks;
            }

            for (COSName name : resources.getXObjectNames()) {
                try {
                    PDXObject object = resources.getXObject(name);
                    if (object instanceof PDImageXObject) {
                        PDImageXObject pdImage = (PDImageXObject) object;
                        BufferedImage image = pdImage.getImage();

                        // 过滤太小的图片
                        if (image != null && image.getWidth() > 10 && image.getHeight() > 10) {
                            byte[] imageBytes = bufferedImageToBytes(image);
                            String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);

                            String format = pdImage.getSuffix();
                            if (format == null || !SUPPORTED_IMAGE_FORMATS.contains(format.toLowerCase())) {
                                format = "png";
                            }

                            // OCR识别
                            String ocrText = null;
                            java.util.List<DocParseResult.TextBlock> ocrBlocks = null;
                            String layoutType = "figure";

                            if (ocrService != null) {
                                try {
                                    OcrService.OcrOption ocrOption = new OcrService.OcrOption();
                                    ocrOption.setLanguage(isEnglish ? "en" : "ch");
                                    ocrOption.setZoomIn(2);

                                    OcrService.OcrResult ocrResult = ocrService.recognize(imageBytes, ocrOption);
                                    if (ocrResult != null && ocrResult.getText() != null) {
                                        ocrText = ocrResult.getText();
                                        if (ocrResult.getBlocks() != null && !ocrResult.getBlocks().isEmpty()) {
                                            ocrBlocks = new java.util.ArrayList<>();
                                            for (OcrService.OcrResult.TextBlock block : ocrResult.getBlocks()) {
                                                ocrBlocks.add(DocParseResult.TextBlock.builder()
                                                        .text(block.getText())
                                                        .confidence(block.getConfidence())
                                                        .bbox(block.getBbox())
                                                        .build());
                                            }
                                        }
                                        layoutType = "ocr_image";
                                    }
                                } catch (Exception e) {
                                    log.warn("页面{}图片OCR识别失败: {}", pageNum, e.getMessage());
                                }
                            }

                            imageChunks.add(DocParseResult.Chunk.builder()
                                    .id(generateChunkId())
                                    .type("image")
                                    .base64(base64)
                                    .content(ocrText)
                                    .blocks(ocrBlocks)
                                    .imageFormat(format)
                                    .layoutType(layoutType)
                                    .pageNumber(pageNum)
                                    .positionIndex(startIndex + imageChunks.size())
                                    .build());
                        }
                    }
                } catch (Exception e) {
                    // 忽略单个图片错误
                }
            }
        } catch (Exception e) {
            log.warn("提取页面{}图片失败: {}", pageNum, e.getMessage());
        }

        return imageChunks;
    }

    /**
     * BufferedImage转byte[]
     */
    private static byte[] bufferedImageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.warn("BufferedImage转byte[]失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDot + 1);
    }

    @Override
    public DocTypeEnum getSupportType() {
        return DocTypeEnum.PDF;
    }
}
