package com.github.pdaodao.springwebplus.ai.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 文档解析结果
 *
 * 结构说明：
 * - chunks: 统一的文档内容块列表，按positionIndex排序，可还原原始文档
 * - 文本、表格、图片都在chunks中，通过type区分
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocParseResult {

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文档类型
     */
    private DocTypeEnum docType;

    /**
     * 文档内容块列表（按原始顺序排列）
     * 包含文本、表格、图片等所有内容
     */
    private List<Chunk> chunks;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 解析耗时（毫秒）
     */
    private long parseTimeMs;

    /**
     * 文档内容块（统一结构）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chunk {
        /**
         * 块ID
         */
        private String id;

        /**
         * 内容类型
         * text: 文本
         * table: 表格
         * image: 图片
         */
        private String type;

        /**
         * 内容
         * - 文本：文本内容
         * - 表格：Markdown格式内容
         * - 图片：Base64编码的图片数据
         */
        private String content;

        /**
         * HTML格式内容（仅表格有效）
         */
        private String htmlContent;

        /**
         * 布局类型（仅文本有效）
         * title: 标题
         * text: 正文
         * reference: 引用
         * header: 页眉
         * footer: 页脚
         * page_number: 页码
         */
        private String layoutType;

        /**
         * 文档中的位置索引（从0开始，按顺序递增）
         * 用于还原原始文档顺序
         */
        private Integer positionIndex;

        /**
         * 页码（PDF有效）
         */
        private Integer pageNumber;

        /**
         * 位置坐标（PDF有效）
         * 使用标准化坐标（0-1）
         */
        private Position position;

        // ==================== 表格专用字段 ====================

        /**
         * 表格行数（仅表格有效）
         */
        private Integer rowCount;

        /**
         * 表格列数（仅表格有效）
         */
        private Integer colCount;

        /**
         * Sheet名称（Excel表格有效）
         */
        private String sheetName;

        // ==================== 图片专用字段 ====================

        /**
         * 图片格式（仅图片有效）
         * 如：png, jpeg, jpg
         */
        private String imageFormat;

        /**
         * 图片起始行（Excel嵌入图片有效）
         */
        private Integer rowFrom;

        /**
         * 图片起始列（Excel嵌入图片有效）
         */
        private Integer colFrom;

        /**
         * 图片结束行（Excel嵌入图片有效）
         */
        private Integer rowTo;

        /**
         * 图片结束列（Excel嵌入图片有效）
         */
        private Integer colTo;

        /**
         * 跨单元格类型（Excel嵌入图片有效）
         * single_cell: 单单元格
         * multi_cell: 跨多单元格
         */
        private String spanType;

        // ==================== 元数据 ====================

        /**
         * 额外元数据
         */
        private Map<String, Object> metadata;
    }

    /**
     * 位置坐标（PDF有效）
     * 使用标准化坐标（0-1），便于跨页面比较
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        /**
         * 对齐方式：LEFT(左对齐), CENTER(居中), RIGHT(右对齐), JUSTIFIED(两端对齐)
         */
        private String alignment;

        /**
         * 首行缩进（磅）
         */
        private Double firstLineIndent;
    }
}
