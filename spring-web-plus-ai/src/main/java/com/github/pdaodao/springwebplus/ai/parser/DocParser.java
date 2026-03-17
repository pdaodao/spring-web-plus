package com.github.pdaodao.springwebplus.ai.parser;

import java.io.File;
import java.util.List;

/**
 * 文档解析器接口
 */
public interface DocParser {

    /**
     * 解析文档
     *
     * @param file   文档文件
     * @param option 解析选项
     * @return 解析结果
     */
    DocParseResult parse(File file, ParseOption option);

    /**
     * 解析文档（使用默认选项）
     *
     * @param file 文档文件
     * @return 解析结果
     */
    default DocParseResult parse(File file) {
        return parse(file, ParseOption.defaultOption());
    }

    /**
     * 获取支持的文档类型
     *
     * @return 文档类型
     */
    DocTypeEnum getSupportType();
}
