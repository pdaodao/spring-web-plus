package com.github.pdaodao.springwebplus.ai.parser;

import java.util.List;

/**
 * Token计数器
 */
public class TokenCounter {
    /** ASCII 字符的字符/token 比例。 */
    private static final double ASCII_CHARS_PER_TOKEN = 4.0;
    /** CJK 字符的字符/token 比例。 */
    private static final double CJK_CHARS_PER_TOKEN = 1.5;
    /** 其他多字节字符的字符/token 比例。 */
    private static final double OTHER_MULTIBYTE_CHARS_PER_TOKEN = 2.0;
    /** JSON 结构化内容的开销加权（工具调用/结果含大量括号引号）。 */
    private static final double JSON_OVERHEAD_MULTIPLIER = 1.1;

    private TokenCounter() {
        // 工具类禁止实例化
    }

    /**
     * 估算单段文本的 token 数。
     *
     * @param text 文本，允许为 null/空
     * @return 估算的 token 数，至少为 0
     */
    public static int countTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int asciiChars = 0;
        int cjkChars = 0;
        int otherChars = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 0x80) {
                asciiChars++;
            } else if (isCjk(c)) {
                cjkChars++;
            } else {
                otherChars++;
            }
        }
        double tokens = asciiChars / ASCII_CHARS_PER_TOKEN
                + cjkChars / CJK_CHARS_PER_TOKEN
                + otherChars / OTHER_MULTIBYTE_CHARS_PER_TOKEN;
        return (int) Math.ceil(tokens);
    }

    /**
     * 判断字符是否属于 CJK 范围（包括中日韩基本汉字 + 扩展 A/B 区）。
     */
    private static boolean isCjk(char c) {
        return (c >= 0x4E00 && c <= 0x9FFF)       // CJK 基本
                || (c >= 0x3400 && c <= 0x4DBF)   // CJK 扩展 A
                || (c >= 0x3000 && c <= 0x303F)   // CJK 符号和标点
                || (c >= 0xFF00 && c <= 0xFFEF);  // 全角字符
    }
}
