package com.github.pdaodao.springwebplus.ai.pojo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "知识文档大类")
public class ChatDocNamespace {
    // 文件
    public static final String file = "file";
    // 文本文档
    public static final String text = "text";
    // 数据表
    public static final String table = "table";
    // sql 语句
    public static final String sql = "sql";
    // excel 文件
    public static final String excel = "excel";
}