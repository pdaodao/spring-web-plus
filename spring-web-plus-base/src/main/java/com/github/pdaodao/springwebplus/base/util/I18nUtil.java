package com.github.pdaodao.springwebplus.base.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * i18n 国际化工具类
 */
public class I18nUtil {

    private static MessageSource messageSource;

    public I18nUtil(MessageSource messageSource) {
        I18nUtil.messageSource = messageSource;
    }

    /**
     * 获取当前语言
     */
    public static Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * 获取当前语言标签 (zh / en)
     */
    public static String getLanguage() {
        return getLocale().getLanguage();
    }

    /**
     * 是否中文环境
     */
    public static boolean isZh() {
        return Locale.CHINA.getLanguage().equals(getLanguage());
    }

    /**
     * 是否英文环境
     */
    public static boolean isEn() {
        return Locale.US.getLanguage().equals(getLanguage()) ||
               Locale.ENGLISH.getLanguage().equals(getLanguage());
    }

    /**
     * 获取消息 (无参数)
     */
    public static String getMessage(String code) {
        return getMessage(code, null, code);
    }

    /**
     * 获取消息 (带默认消息)
     */
    public static String getMessage(String code, String defaultMsg) {
        return getMessage(code, null, defaultMsg);
    }

    /**
     * 获取消息 (varargs 参数)
     * <p>
     * 占位符格式: {0}, {1}, {2} ...
     * 示例: messages文件中定义 greeting=Hello {0}, you have {1} new messages
     *      调用 getMessage("greeting", "John", 5) -> "Hello John, you have 5 new messages"
     */
    public static String getMessage(String code, Object... args) {
        return getMessage(code, args, code);
    }

    /**
     * 获取消息 (带参数和默认消息)
     */
    public static String getMessage(String code, Object[] args, String defaultMsg) {
        if (messageSource == null) {
            return defaultMsg;
        }
        try {
            return messageSource.getMessage(code, args, defaultMsg, getLocale());
        } catch (Exception e) {
            return defaultMsg;
        }
    }
}
