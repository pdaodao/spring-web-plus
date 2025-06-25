package com.github.pdaodao.springwebplus.ai.doc;

import lombok.Data;
import java.util.List;

/**
 * 文档
 */
@Data
public class AiDoc {
    private String title;

    // 文档段落列表
    private List<AiDocText> list;
}
