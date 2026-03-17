package com.github.pdaodao.springwebplus.ai.parser;

import lombok.Data;

/**
 * 文档解析选项
 *
 * 注意：长文本分块由外部Splitter组件负责，不在此配置
 */
@Data
public class ParseOption {

    /**
     * 是否提取标题
     */
    private boolean extractTitle = true;

    /**
     * 是否提取表格
     */
    private boolean extractTable = true;

    /**
     * 是否提取图片
     */
    private boolean extractImage = false;

    /**
     * 是否保留原始格式
     */
    private boolean preserveFormat = false;

    /**
     * 最小段落长度（用于过滤噪声）
     */
    private int minParagraphLength = 10;

    /**
     * PDF专用：缩放因子（zoomin参数，用于提高OCR精度）
     * 值越大，图像越清晰，解析越慢
     * 建议值：1-5
     */
    private int pdfZoomIn = 3;

    /**
     * PDF专用：是否自动旋转表格
     */
    private boolean autoRotateTable = true;

    /**
     * Excel专用：每块行数
     */
    private int excelChunkRows = 256;

    /**
     * Python服务地址（用于解析PDF等复杂文档）
     */
    private String pythonServiceUrl = "http://localhost:8000";

    public static ParseOption defaultOption() {
        return new ParseOption();
    }
}
