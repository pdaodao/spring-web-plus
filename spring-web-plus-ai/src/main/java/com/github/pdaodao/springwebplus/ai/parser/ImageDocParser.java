package com.github.pdaodao.springwebplus.ai.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;

/**
 * 图片文档解析器
 * 支持常见图片格式：png, jpg, jpeg, gif, bmp, webp, tiff
 *
 * 功能：
 * - 提取图片Base64
 * - 可选：调用OCR识别图片文字
 *
 * 注意：长文本分块由外部Splitter组件负责
 */
@Slf4j
public class ImageDocParser extends AbstractDocParser {

    /**
     * 支持的图片格式
     */
    private static final List<String> SUPPORTED_FORMATS = List.of(
            "png", "jpg", "jpeg", "gif", "bmp", "webp", "tiff", "tif"
    );

    /**
     * OCR服务（可选，用于识别图片中的文字）
     */
    private OcrService ocrService;

    /**
     * 设置OCR服务
     */
    public void setOcrService(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    /**
     * 获取OCR服务
     */
    public OcrService getOcrService() {
        return ocrService;
    }

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();

        option = option != null ? option : new ParseOption();

        // 检查文件格式
        String extension = getFileExtension(file.getName()).toLowerCase();
        if (!SUPPORTED_FORMATS.contains(extension)) {
            throw new RuntimeException("不支持的图片格式: " + extension);
        }

        try {
            // 读取图片文件
            byte[] imageData = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(imageData);

            List<DocParseResult.Chunk> chunks = new ArrayList<>();
            int positionIndex = 0;

            // 如果有OCR服务，识别图片中的文字
            String ocrText = null;
            List<DocParseResult.TextBlock> ocrBlocks = null;

            if (ocrService != null) {
                try {
                    OcrService.OcrOption ocrOption = new OcrService.OcrOption();
                    OcrService.OcrResult ocrResult = ocrService.recognize(imageData, ocrOption);
                    if (ocrResult != null) {
                        ocrText = ocrResult.getText();
                        if (ocrResult.getBlocks() != null && !ocrResult.getBlocks().isEmpty()) {
                            ocrBlocks = new ArrayList<>();
                            for (OcrService.OcrResult.TextBlock block : ocrResult.getBlocks()) {
                                ocrBlocks.add(DocParseResult.TextBlock.builder()
                                        .text(block.getText())
                                        .confidence(block.getConfidence())
                                        .bbox(block.getBbox())
                                        .build());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("OCR识别失败: {}", e.getMessage());
                }
            }

            // 构建Chunk
            DocParseResult.Chunk chunk = DocParseResult.Chunk.builder()
                    .id(generateChunkId())
                    .type("image")
                    .base64(base64)
                    .content(ocrText)
                    .blocks(ocrBlocks)
                    .imageFormat(extension)
                    .layoutType(ocrText != null ? "ocr_image" : "image")
                    .positionIndex(positionIndex)
                    .build();

            chunks.add(chunk);

            return DocParseResult.builder()
                    .title(null)
                    .docType(DocTypeEnum.UNKNOWN)
                    .chunks(chunks)
                    .fileName(file.getName())
                    .parseTimeMs(System.currentTimeMillis() - startTime)
                    .build();

        } catch (IOException e) {
            log.error("解析图片文件失败: {}", file.getName(), e);
            throw new RuntimeException("解析图片文件失败: " + e.getMessage(), e);
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
        return DocTypeEnum.UNKNOWN;
    }
}
