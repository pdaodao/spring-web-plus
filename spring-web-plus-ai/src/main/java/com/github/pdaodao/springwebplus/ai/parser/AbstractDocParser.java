package com.github.pdaodao.springwebplus.ai.parser;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 抽象文档解析器
 */
@Slf4j
public abstract class AbstractDocParser implements DocParser {

    /**
     * 生成块ID
     */
    protected String generateChunkId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 读取文件内容
     */
    protected String readContent(File file, Charset charset) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            log.error("读取文件失败: {}", file.getName(), e);
        }
        return content.toString();
    }

    /**
     * 读取文件内容（自动检测编码）
     * 对应Python: get_text() + find_codec()
     */
    protected String readContentAutoDetect(File file) {
        try {
            byte[] bytes = new byte[1024];
            try (FileInputStream fis = new FileInputStream(file)) {
                int read = fis.read(bytes);
                if (read <= 0) {
                    return readContent(file, StandardCharsets.UTF_8);
                }
                byte[] fileBytes = Arrays.copyOf(bytes, read);
                // 检测编码
                String encoding = detectEncoding(fileBytes);
                Charset charset = Charset.forName(encoding);
                return readContent(file, charset);
            }
        } catch (Exception e) {
            log.warn("自动检测编码失败，使用UTF-8: {}", e.getMessage());
            return readContent(file, StandardCharsets.UTF_8);
        }
    }

    /**
     * 检测文件编码
     * 对应Python: find_codec()
     */
    private String detectEncoding(byte[] bytes) {
        // 优先尝试UTF-8
        try {
            String text = new String(bytes, StandardCharsets.UTF_8);
            if (!text.contains("�")) {
                return "UTF-8";
            }
        } catch (Exception e) {
            // 继续尝试其他编码
        }

        // 尝试GBK（中文Windows常用）
        try {
            String text = new String(bytes, Charset.forName("GBK"));
            if (!text.contains("�")) {
                return "GBK";
            }
        } catch (Exception e) {
            // 继续尝试其他编码
        }

        // 尝试GB2312
        try {
            String text = new String(bytes, Charset.forName("GB2312"));
            if (!text.contains("�")) {
                return "GB2312";
            }
        } catch (Exception e) {
            // 继续尝试其他编码
        }

        // 默认返回UTF-8
        return "UTF-8";
    }

    /**
     * 按行分割段落
     */
    protected List<String> splitByLines(String content) {
        List<String> paragraphs = new ArrayList<>();
        if (content == null || content.isEmpty()) {
            return paragraphs;
        }

        String[] lines = content.split("\n");
        StringBuilder currentPara = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                // 空行作为段落分隔符
                if (currentPara.length() > 0) {
                    paragraphs.add(currentPara.toString().trim());
                    currentPara = new StringBuilder();
                }
            } else {
                if (currentPara.length() > 0) {
                    currentPara.append("\n");
                }
                currentPara.append(line);
            }
        }

        // 处理最后一个段落
        if (currentPara.length() > 0) {
            paragraphs.add(currentPara.toString().trim());
        }

        return paragraphs;
    }

    /**
     * 清理文本
     */
    protected String cleanText(String text) {
        if (text == null) {
            return "";
        }
        // 替换多个空白字符为单个空格
        text = text.replaceAll("\\s+", " ");
        // 去除首尾空白
        return text.trim();
    }

    /**
     * 检测是否为标题（简单规则）
     */
    protected boolean isTitle(String line) {
        if (line == null || line.isEmpty()) {
            return false;
        }
        // 短行且以特殊字符开头或全大写
        return (line.length() < 100 && (line.startsWith("#") ||
                line.matches("^[A-Z][A-Z\\s]+$") ||
                (line.length() < 50 && !line.contains("."))));
    }

    /**
     * 清理非法字符（Excel专用）
     * 对应Python: ILLEGAL_CHARACTERS_RE = re.compile(r"[\000-\010]|[\013-\014]|[\016-\037]")
     */
    protected String cleanIllegalChars(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            // 排除 \x00-\x08, \x0b-\x0c, \x0e-\x1f
            if ((c >= '\0' && c <= '\b') || (c == '\u000B' || c == '\u000C') || (c >= '\u000E' && c <= '\u001F')) {
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * HTML转义
     */
    protected String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
