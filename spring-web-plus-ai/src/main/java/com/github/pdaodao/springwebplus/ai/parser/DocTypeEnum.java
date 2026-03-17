package com.github.pdaodao.springwebplus.ai.parser;

/**
 * 文档类型枚举
 */
public enum DocTypeEnum {

    TXT("txt", "纯文本"),
    HTML("html", "HTML"),
    HTM("htm", "HTML"),
    XLSX("xlsx", "Excel"),
    XLS("xls", "Excel"),
    PDF("pdf", "PDF"),
    DOCX("docx", "Word"),
    DOC("doc", "Word"),
    MARKDOWN("md", "Markdown"),
    CSV("csv", "CSV"),
    UNKNOWN("unknown", "未知");

    private final String extension;
    private final String description;

    DocTypeEnum(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }

    public static DocTypeEnum getByExtension(String extension) {
        if (extension == null) {
            return UNKNOWN;
        }
        for (DocTypeEnum type : values()) {
            if (type.extension.equalsIgnoreCase(extension)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public boolean isSimpleParse() {
        return this == TXT || this == HTML || this == HTM || this == XLSX || this == XLS || this == MARKDOWN || this == CSV;
    }

    public boolean isPdf() {
        return this == PDF;
    }

    public boolean isDocx() {
        return this == DOCX || this == DOC;
    }
}
