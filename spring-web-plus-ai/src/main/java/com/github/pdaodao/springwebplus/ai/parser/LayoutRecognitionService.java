package com.github.pdaodao.springwebplus.ai.parser;

/**
 * 布局识别服务接口
 * 用于识别文档中的布局元素：标题、段落、表格、图片、页眉、页脚等
 *
 * 可对接外部Python服务（如RAGFlow的layout_recognizer，基于YOLO/ONNX模型）
 */
public interface LayoutRecognitionService {

    /**
     * 识别图片中的布局元素
     *
     * @param imageData 图片数据
     * @param option    识别选项
     * @return 布局识别结果
     */
    LayoutResult recognize(byte[] imageData, LayoutOption option);

    /**
     * 识别图片中的布局元素（Base64格式）
     *
     * @param base64Image Base64编码的图片
     * @param option      识别选项
     * @return 布局识别结果
     */
    LayoutResult recognize(String base64Image, LayoutOption option);

    /**
     * 布局识别选项
     */
    class LayoutOption {
        /**
         * 是否检测表格
         */
        private boolean detectTable = true;

        /**
         * 是否检测图片
         */
        private boolean detectImage = true;

        /**
         * 缩放因子（用于提高精度）
         */
        private int zoomIn = 1;

        public boolean isDetectTable() {
            return detectTable;
        }

        public void setDetectTable(boolean detectTable) {
            this.detectTable = detectTable;
        }

        public boolean isDetectImage() {
            return detectImage;
        }

        public void setDetectImage(boolean detectImage) {
            this.detectImage = detectImage;
        }

        public int getZoomIn() {
            return zoomIn;
        }

        public void setZoomIn(int zoomIn) {
            this.zoomIn = zoomIn;
        }
    }

    /**
     * 布局识别结果
     */
    class LayoutResult {
        /**
         * 布局元素列表
         */
        private java.util.List<LayoutElement> elements;

        public java.util.List<LayoutElement> getElements() {
            return elements;
        }

        public void setElements(java.util.List<LayoutElement> elements) {
            this.elements = elements;
        }
    }

    /**
     * 布局元素
     */
    class LayoutElement {
        /**
         * 元素类型：text, title, table, image, header, footer, footnote
         */
        private String type;

        /**
         * 元素内容（文本或Base64图片）
         */
        private String content;

        /**
         * 位置信息 [x1, y1, x2, y2]
         */
        private int[] bbox;

        /**
         * 页码
         */
        private int pageNumber;

        /**
         * 置信度
         */
        private double confidence;

        /**
         * 子元素（如表格的单元格）
         */
        private java.util.List<LayoutElement> children;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int[] getBbox() {
            return bbox;
        }

        public void setBbox(int[] bbox) {
            this.bbox = bbox;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public java.util.List<LayoutElement> getChildren() {
            return children;
        }

        public void setChildren(java.util.List<LayoutElement> children) {
            this.children = children;
        }
    }
}
