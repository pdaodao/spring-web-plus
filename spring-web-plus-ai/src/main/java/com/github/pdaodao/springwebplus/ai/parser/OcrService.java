package com.github.pdaodao.springwebplus.ai.parser;

/**
 * OCR服务接口
 * 用于识别图片中的文字
 *
 * 可对接外部Python OCR服务（如PaddleOCR、PaddlePaddle等）
 * 也可对接云服务（阿里云OCR、腾讯云OCR等）
 */
public interface OcrService {

    /**
     * 识别图片中的文字
     *
     * @param imageData 图片数据（Base64或二进制）
     * @param option    OCR选项
     * @return 识别结果
     */
    OcrResult recognize(byte[] imageData, OcrOption option);

    /**
     * 识别图片中的文字（Base64格式）
     *
     * @param base64Image Base64编码的图片
     * @param option      OCR选项
     * @return 识别结果
     */
    OcrResult recognize(String base64Image, OcrOption option);

    /**
     * OCR选项
     */
    class OcrOption {
        /**
         * 语言类型：ch, en, ja, ko, etc.
         */
        private String language = "ch";

        /**
         * 是否检测文本方向
         */
        private boolean detectDirection = true;

        /**
         * 是否识别表格
         */
        private boolean enableTable = false;

        /**
         * 缩放因子（用于提高精度）
         */
        private int zoomIn = 1;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public boolean isDetectDirection() {
            return detectDirection;
        }

        public void setDetectDirection(boolean detectDirection) {
            this.detectDirection = detectDirection;
        }

        public boolean isEnableTable() {
            return enableTable;
        }

        public void setEnableTable(boolean enableTable) {
            this.enableTable = enableTable;
        }

        public int getZoomIn() {
            return zoomIn;
        }

        public void setZoomIn(int zoomIn) {
            this.zoomIn = zoomIn;
        }
    }

    /**
     * OCR识别结果
     */
    class OcrResult {
        /**
         * 识别出的文本
         */
        private String text;

        /**
         * 文本块列表（包含位置信息）
         */
        private java.util.List<TextBlock> blocks;

        /**
         * 表格结果（如果enableTable=true）
         */
        private String tableHtml;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public java.util.List<TextBlock> getBlocks() {
            return blocks;
        }

        public void setBlocks(java.util.List<TextBlock> blocks) {
            this.blocks = blocks;
        }

        public String getTableHtml() {
            return tableHtml;
        }

        public void setTableHtml(String tableHtml) {
            this.tableHtml = tableHtml;
        }

        /**
         * 文本块（包含位置信息）
         */
        public static class TextBlock {
            /**
             * 文本内容
             */
            private String text;

            /**
             * 置信度
             */
            private double confidence;

            /**
             * 位置信息 [x1, y1, x2, y2]
             */
            private int[] bbox;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public double getConfidence() {
                return confidence;
            }

            public void setConfidence(double confidence) {
                this.confidence = confidence;
            }

            public int[] getBbox() {
                return bbox;
            }

            public void setBbox(int[] bbox) {
                this.bbox = bbox;
            }
        }
    }
}
