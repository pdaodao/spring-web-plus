package com.github.pdaodao.springwebplus.ai.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 纯文本解析器
 * 对应RAGFlow: deepdoc/parser/txt_parser.py
 *
 * 功能：
 * - 按分隔符分割段落
 * - 提取标题
 * - 保留位置信息
 *
 * 注意：长文本分块由外部Splitter组件负责
 */
@Slf4j
public class TxtDocParser extends AbstractDocParser {

    /**
     * 默认分隔符（句子结束标点）
     * 中文：。；！？……——
     * 英文：.!?;
     * 日文：。！？———
     * 韩文：.;!?;
     */
    private static final String DEFAULT_DELIMITER = "\n。；！？……——.!?;";

    @Override
    public DocParseResult parse(File file, ParseOption option) {
        long startTime = System.currentTimeMillis();

        // 读取文件内容
        String content = readContentAutoDetect(file);

        // 按分隔符分割段落
        List<DocParseResult.Chunk> chunks = parserTxt(content);

        // 提取标题（第一个段落作为标题）
        String title = null;
        if (!chunks.isEmpty()) {
            DocParseResult.Chunk firstChunk = chunks.get(0);
            if ("title".equals(firstChunk.getLayoutType())) {
                title = firstChunk.getContent();
            }
        }

        return DocParseResult.builder()
                .title(title)
                .docType(DocTypeEnum.TXT)
                .chunks(chunks)
                .fileName(file.getName())
                .parseTimeMs(System.currentTimeMillis() - startTime)
                .build();
    }

    /**
     * 解析纯文本，按分隔符分割段落
     * 对应RAGFlow: RAGFlowTxtParser.parser_txt()
     *
     * 注意：不进行token分块，分块由外部Splitter负责
     *
     * @param txt 文本内容
     * @return 段落列表
     */
    public List<DocParseResult.Chunk> parserTxt(String txt) {
        if (txt == null || txt.isEmpty()) {
            return new ArrayList<>();
        }

        // 按分隔符分割
        String[] sections = splitByDelimiter(txt);

        // 转换为Chunk对象
        List<DocParseResult.Chunk> chunks = new ArrayList<>();
        int positionIndex = 0;

        for (String section : sections) {
            if (section == null || section.isEmpty()) {
                continue;
            }

            // 跳过分隔符本身
            if (isDelimiter(section)) {
                continue;
            }

            // 过滤过短的段落
            if (section.trim().length() < 5) {
                continue;
            }

            // 判断布局类型
            String layoutType = detectLayoutType(section, positionIndex);

            chunks.add(DocParseResult.Chunk.builder()
                    .id(generateChunkId())
                    .type("text")
                    .content(section.trim())
                    .layoutType(layoutType)
                    .positionIndex(positionIndex++)
                    .build());
        }

        return chunks;
    }

    /**
     * 按分隔符分割文本
     */
    private String[] splitByDelimiter(String txt) {
        // 使用Pattern.quote转义分隔符
        String regex = "[" + Pattern.quote(DEFAULT_DELIMITER) + "]";
        // limit=-1保留所有空部分
        return Pattern.compile(regex).split(txt, -1);
    }

    /**
     * 判断是否为分隔符
     */
    private boolean isDelimiter(String text) {
        if (text == null || text.length() != 1) {
            return false;
        }
        return DEFAULT_DELIMITER.contains(text);
    }

    /**
     * 检测布局类型
     */
    private String detectLayoutType(String text, int index) {
        if (text == null || text.isEmpty()) {
            return "text";
        }

        // 第一个段落且文本较短，视为标题
        if (index == 0 && text.length() < 100) {
            return "title";
        }

        // 短文本且以#开头或全大写，视为标题
        if (text.length() < 100) {
            if (text.startsWith("#") || text.matches("^[A-Z][A-Z\\s]+$")) {
                return "title";
            }
        }

        return "text";
    }

    @Override
    public DocTypeEnum getSupportType() {
        return DocTypeEnum.TXT;
    }
}
