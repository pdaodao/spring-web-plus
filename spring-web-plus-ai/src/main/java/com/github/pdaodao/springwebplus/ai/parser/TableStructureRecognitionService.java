package com.github.pdaodao.springwebplus.ai.parser;

/**
 * 表格结构识别服务接口
 * 用于识别表格的结构：行、列、单元格、合并单元格等
 *
 * 可对接外部Python服务（如RAGFlow的table_structure_recognizer，基于PaddleOCR）
 */
public interface TableStructureRecognitionService {

    /**
     * 识别表格结构
     *
     * @param imageData 表格图片数据
     * @param option    识别选项
     * @return 表格结构结果
     */
    TableResult recognize(byte[] imageData, TableOption option);

    /**
     * 识别表格结构（Base64格式）
     *
     * @param base64Image Base64编码的表格图片
     * @param option      识别选项
     * @return 表格结构结果
     */
    TableResult recognize(String base64Image, TableOption option);

    /**
     * 表格结构识别选项
     */
    class TableOption {
        /**
         * 是否检测合并单元格
         */
        private boolean detectMergedCells = true;

        /**
         * 缩放因子（用于提高精度）
         */
        private int zoomIn = 1;

        /**
         * 语言
         */
        private String language = "ch";

        public boolean isDetectMergedCells() {
            return detectMergedCells;
        }

        public void setDetectMergedCells(boolean detectMergedCells) {
            this.detectMergedCells = detectMergedCells;
        }

        public int getZoomIn() {
            return zoomIn;
        }

        public void setZoomIn(int zoomIn) {
            this.zoomIn = zoomIn;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
    }

    /**
     * 表格结构识别结果
     */
    class TableResult {
        /**
         * HTML格式的表格
         */
        private String html;

        /**
         * Markdown格式的表格
         */
        private String markdown;

        /**
         * 二维数组表示的表格内容
         */
        private String[][] cells;

        /**
         * 表格是否包含合并单元格
         */
        private boolean hasMergedCells;

        /**
         * 行数
         */
        private int rowCount;

        /**
         * 列数
         */
        private int colCount;

        public String getHtml() {
            return html;
        }

        public void setHtml(String html) {
            this.html = html;
        }

        public String getMarkdown() {
            return markdown;
        }

        public void setMarkdown(String markdown) {
            this.markdown = markdown;
        }

        public String[][] getCells() {
            return cells;
        }

        public void setCells(String[][] cells) {
            this.cells = cells;
        }

        public boolean isHasMergedCells() {
            return hasMergedCells;
        }

        public void setHasMergedCells(boolean hasMergedCells) {
            this.hasMergedCells = hasMergedCells;
        }

        public int getRowCount() {
            return rowCount;
        }

        public void setRowCount(int rowCount) {
            this.rowCount = rowCount;
        }

        public int getColCount() {
            return colCount;
        }

        public void setColCount(int colCount) {
            this.colCount = colCount;
        }
    }
}
