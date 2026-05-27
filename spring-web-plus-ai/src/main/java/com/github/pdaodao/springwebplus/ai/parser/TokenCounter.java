package com.github.pdaodao.springwebplus.ai.parser;

/**
 * Token计数器
 * 对应Python: common/token_utils.py - num_tokens_from_string()
 * 使用cl100k_base编码规则计算token数
 *
 * 注意：这是简化实现，使用字符/4作为估算
 * 精确实现需要使用tiktoken的Java绑定或实现完整的cl100k_base编码
 */
public class TokenCounter {

    /**
     * 计算文本的token数
     * 对应Python: num_tokens_from_string()
     *
     * @param text 文本内容
     * @return token数
     */
    public static int countTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // cl100k_base编码规则：
        // - 大部分字符占1个token
        // - 汉字占2-4个token
        // - Emoji占2-4个token

        int tokens = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // ASCII字符
            if (c < 128) {
                tokens += 1;
            }
            // 中文、日文、韩文（CJK统一汉字）
            else if (isCJK(c)) {
                // 汉字通常占2个token
                tokens += 2;
            }
            // Emoji和其他字符
            else if (c >= 0x10000) {
                // 代理对，占2个token
                tokens += 2;
                i++; // 跳过代理对的第二个字符
            }
            else {
                // 其他Unicode字符，通常占2个token
                tokens += 2;
            }
        }

        return tokens;
    }

    /**
     * 判断是否为CJK字符
     */
    private static boolean isCJK(char c) {
        // CJK统一汉字基本区
        if (c >= 0x4E00 && c <= 0x9FFF) return true;
        // CJK统一汉字扩展A区
        if (c >= 0x3400 && c <= 0x4DBF) return true;
        // 日文平假名
        if (c >= 0x3040 && c <= 0x309F) return true;
        // 日文片假名
        if (c >= 0x30A0 && c <= 0x30FF) return true;
        // 韩文音节
        if (c >= 0xAC00 && c <= 0xD7AF) return true;
        // 韩文字母
        if (c >= 0x1100 && c <= 0x11FF) return true;
        // 标点符号
        if (c >= 0x3000 && c <= 0x303F) return true;
        return false;
    }

    /**
     * 估算token数（简化版本：字符数/4 + 单词数）
     * 更快但不够精确
     */
    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // 英文单词数（空格分隔）
        String[] words = text.split("\\s+");
        int wordCount = words.length;

        // 中文字符数
        int chineseCharCount = 0;
        for (char c : text.toCharArray()) {
            if (c >= 0x4E00 && c <= 0x9FFF) {
                chineseCharCount++;
            }
        }

        // 估算：英文单词*1.3 + 中文字符*2
        return (int) (wordCount * 1.3 + chineseCharCount * 2);
    }


    public static void main(String[] args) {
        System.out.println(countTokens("hello world"));
    }
}
