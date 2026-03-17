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
 * 基于Apache PDFBox实现
 * 支持外部算法服务（OCR）进行增强
 */
@Slf4j
public class PdfDocParser extends AbstractDocParser {

    private static final java.util.List<String> SUPPORTED_IMAGE_FORMATS = java.util.List.of(
            "png", "jpg", "jpeg", "gif", "bmp"
    );

    private OcrService ocrService;
    private boolean isEnglish = false;

    public PdfDocParser() {
    }

    public PdfDocParser(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    public void setOcrService(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    public OcrService getOcrService() {
        return ocrService;
    }

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();
        option = option != null ? option : new ParseOption();

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

    private DocParseResult parsePdf(File file, ParseOption option, long startTime) {
        String fileName = file.getName();

        try (PDDocument document = org.apache.pdfbox.Loader.loadPDF(file)) {
            java.util.List<DocParseResult.Chunk> chunks = new java.util.ArrayList<>();
            String title = null;
            int positionIndex = 0;

            int pageCount = document.getNumberOfPages();
            detectLanguage(document);

            DocParseResult.Chunk lastChunk = null;

            for (int pageNum = 1; pageNum <= pageCount; pageNum++) {
                PDPage page = document.getPage(pageNum - 1);

                // 1. 提取文本
                java.util.List<String> pageParagraphs = extractPageText(document, pageNum);

                for (String para : pageParagraphs) {
                    if (para == null || para.trim().isEmpty()) {
                        continue;
                    }
                    if (isNoise(para.trim())) {
                        continue;
                    }

                    String layoutType = detectLayoutType(para.trim());

                    if (title == null && "title".equals(layoutType)) {
                        title = para.trim();
                    }

                    // 跨页合并
                    if (lastChunk != null && "text".equals(lastChunk.getType())
                            && "text".equals(layoutType)
                            && canMerge(lastChunk.getContent(), para.trim())) {
                        lastChunk.setContent(lastChunk.getContent() + "\n" + para.trim());
                    } else {
                        DocParseResult.Chunk chunk = DocParseResult.Chunk.builder()
                                .id(generateChunkId())
                                .type("text")
                                .content(para.trim())
                                .layoutType(layoutType)
                                .pageNumber(pageNum)
                                .positionIndex(positionIndex++)
                                .build();
                        chunks.add(chunk);
                        lastChunk = chunk;
                    }
                }

                // 2. 提取图片
                if (option.isExtractImage()) {
                    java.util.List<DocParseResult.Chunk> imageChunks = extractImages(page, pageNum);
                    for (DocParseResult.Chunk imageChunk : imageChunks) {
                        imageChunk.setPositionIndex(positionIndex++);
                        chunks.add(imageChunk);
                        lastChunk = imageChunk;
                    }
                }
            }

            if (title == null && !chunks.isEmpty()) {
                for (DocParseResult.Chunk chunk : chunks) {
                    if ("text".equals(chunk.getType()) && "title".equals(chunk.getLayoutType())) {
                        title = chunk.getContent();
                        break;
                    }
                }
            }

            for (int i = 0; i < chunks.size(); i++) {
                chunks.get(i).setPositionIndex(i);
            }

            if (title == null) {
                title = fileName;
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

    private java.util.List<String> extractPageText(PDDocument document, int pageNum) {
        java.util.List<String> paragraphs = new java.util.ArrayList<>();

        try {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(pageNum);
            stripper.setEndPage(pageNum);
            String text = stripper.getText(document);

            if (text != null && !text.isEmpty()) {
                String[] lines = text.split("\n");
                java.util.List<String> paraLines = new java.util.ArrayList<>();

                for (String line : lines) {
                    if (line == null) continue;
                    line = line.trim();
                    if (line.isEmpty()) {
                        if (!paraLines.isEmpty()) {
                            paragraphs.add(java.lang.String.join("\n", paraLines));
                            paraLines.clear();
                        }
                    } else {
                        paraLines.add(line);
                    }
                }

                if (!paraLines.isEmpty()) {
                    paragraphs.add(java.lang.String.join("\n", paraLines));
                }
            }
        } catch (Exception e) {
            log.warn("提取页面{}文本失败: {}", pageNum, e.getMessage());
        }

        return paragraphs;
    }

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

    private String detectLayoutType(String text) {
        if (text == null || text.isEmpty()) return "text";

        if (text.matches("^第[一二三四五六七八九十百]+[章条节]")) return "title";
        if (text.matches("^(摘要|Abstract|目录|前言|参考文献|附录|致谢)$")) return "title";
        if (text.matches("^(Chapter|Section|Abstract|Contents|Introduction|References|Appendix|Acknowledgments)\\s*.*")) return "title";
        if (text.matches("^[0-9]+[.、].*") && text.length() < 80) return "title";
        if (text.length() < 60 && !text.endsWith(".") && !text.endsWith("。")) return "title";

        return "text";
    }

    private boolean isNoise(String text) {
        if (text == null || text.isEmpty()) return true;
        text = text.trim();
        if (text.matches("^[0-9]+$")) return true;
        if (text.matches("^[\\-\\*\\._]+$")) return true;
        if (text.matches("^https?://.*")) return true;
        return false;
    }

    private boolean canMerge(String text1, String text2) {
        if (text1 == null || text2 == null) return false;
        text1 = text1.trim();
        text2 = text2.trim();
        if (text1.isEmpty() || text2.isEmpty()) return false;

        char endChar = text1.charAt(text1.length() - 1);
        if ("。！？!?".indexOf(endChar) >= 0) return false;
        if (text1.endsWith("...") || text1.endsWith("……")) return false;
        if (text2.matches("^[0-9]+[.、].*")) return false;
        if (text2.matches("^[（\\(][0-9]+[）\\)].*")) return false;

        return true;
    }

    private java.util.List<DocParseResult.Chunk> extractImages(PDPage page, int pageNum) {
        java.util.List<DocParseResult.Chunk> imageChunks = new java.util.ArrayList<>();

        try {
            PDResources resources = page.getResources();
            if (resources == null) return imageChunks;

            Iterable<COSName> xObjectNames = resources.getXObjectNames();
            if (xObjectNames == null) return imageChunks;

            for (COSName name : xObjectNames) {
                try {
                    PDXObject object = resources.getXObject(name);
                    if (object instanceof PDImageXObject) {
                        PDImageXObject pdImage = (PDImageXObject) object;
                        BufferedImage image = pdImage.getImage();

                        if (image != null && image.getWidth() > 10 && image.getHeight() > 10) {
                            byte[] imageBytes = bufferedImageToBytes(image);
                            String base64 = java.util.Base64.getEncoder().encodeToString(imageBytes);

                            String format = pdImage.getSuffix();
                            if (format == null || !SUPPORTED_IMAGE_FORMATS.contains(format.toLowerCase())) {
                                format = "png";
                            }

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

    private static byte[] bufferedImageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.warn("BufferedImage转byte[]失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) return "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot == -1 || lastDot == fileName.length() - 1) return "";
        return fileName.substring(lastDot + 1);
    }

    @Override
    public DocTypeEnum getSupportType() {
        return DocTypeEnum.PDF;
    }
}
