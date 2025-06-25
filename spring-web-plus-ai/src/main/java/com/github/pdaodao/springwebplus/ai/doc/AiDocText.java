package com.github.pdaodao.springwebplus.ai.doc;

import lombok.Data;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 文档文本段
 */
@Data
public class AiDocText {
    private String id;

    // 内容
    private String content;

    // 向量
    private float[] embedding;

    private Map<String, Object> metadata;
}