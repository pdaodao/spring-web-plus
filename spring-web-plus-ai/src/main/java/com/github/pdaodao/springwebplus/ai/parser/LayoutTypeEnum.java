package com.github.pdaodao.springwebplus.ai.parser;

/**
 * 布局类型枚举
 * 对应RAGFlow的layout_type
 */
public enum LayoutTypeEnum {

    TITLE("title", "标题"),
    TEXT("text", "文本"),
    TABLE("table", "表格"),
    FIGURE("figure", "图片"),
    FIGURE_CAPTION("figure_caption", "图片标题"),
    TABLE_CAPTION("table_caption", "表格标题"),
    REFERENCE("reference", "引用"),
    HEADER("header", "页眉"),
    FOOTER("footer", "页脚"),
    PAGE_NUMBER("page_number", "页码"),
    SEPARATOR("separator", "分隔符"),
    UNKNOWN("unknown", "未知");

    private final String type;
    private final String description;

    LayoutTypeEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static LayoutTypeEnum getByType(String type) {
        if (type == null) {
            return UNKNOWN;
        }
        for (LayoutTypeEnum layoutType : values()) {
            if (layoutType.type.equalsIgnoreCase(type)) {
                return layoutType;
            }
        }
        return UNKNOWN;
    }
}
