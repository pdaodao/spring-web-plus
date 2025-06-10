package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

public enum ChatContentType {
    @Schema(description = "文本")
    text,
    @Schema(description = "txt")
    txt,
    @Schema(description = "数据表格")
    table,
    @Schema(description = "图片")
    image,
    @Schema(description = "语音")
    audio,
    @Schema(description = "视频")
    video,
    @Schema(description = "pdf")
    pdf,
    @Schema(description = "excel")
    excel,
    @Schema(description = "word文档")
    word
}
