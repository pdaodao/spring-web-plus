package com.github.pdaodao.springwebplus.base.frame;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 敏感字段序列化
 */
@AllArgsConstructor
@NoArgsConstructor
public class SensitiveFieldSerialize extends JsonSerializer<String> implements ContextualSerializer {
    private SensitiveField sensitiveField;

    public static String firstMask(String str) {
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        return StrUtil.hide(str, 1, str.length());
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        switch (this.sensitiveField.value()) {
            case CHINESE_NAME: {
                jsonGenerator.writeString(DesensitizedUtil.chineseName(s));
                return;
            }
            case USER_ID: {
                jsonGenerator.writeNull();
                return;
            }
            case PASSWORD: {
                jsonGenerator.writeString(DesensitizedUtil.password(s));
                return;
            }
            case ID_CARD: {
                jsonGenerator.writeString(DesensitizedUtil.idCardNum(s, 6, 2));
                return;
            }
            case FIXED_PHONE: {
                jsonGenerator.writeString(DesensitizedUtil.fixedPhone(s));
                return;
            }
            case MOBILE_PHONE: {
                jsonGenerator.writeString(DesensitizedUtil.mobilePhone(s));
                return;
            }
            case IP: {
                jsonGenerator.writeString(ip(s));
                return;
            }
            case ADDRESS: {
                jsonGenerator.writeString(DesensitizedUtil.address(s, 6));
                return;
            }
            case CAR_LICENSE: {
                jsonGenerator.writeString(DesensitizedUtil.carLicense(s));
                return;
            }
            case EMAIL: {
                jsonGenerator.writeString(DesensitizedUtil.email(s));
                return;
            }
            case BANK_CARD: {
                jsonGenerator.writeString(DesensitizedUtil.bankCard(s));
                return;
            }
            case CNAPS_CODE: {
                jsonGenerator.writeString(this.hide(s, 4, 4));
                return;
            }
            case OTHER: {
                jsonGenerator.writeString(this.hide(s, sensitiveField.front(), sensitiveField.end()));
                return;
            }
            default: {
                jsonGenerator.writeString(s);
            }
        }

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty)
            throws JsonMappingException {
        if (beanProperty != null) { // 为空直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                // 非 String 类直接跳过
                SensitiveField sensitiveInfo = beanProperty.getAnnotation(SensitiveField.class);
                if (sensitiveInfo == null) {
                    sensitiveInfo = beanProperty.getContextAnnotation(SensitiveField.class);
                }
                if (sensitiveInfo != null) { // 如果能得到注解，就将注解的 value 传入
                    // SensitiveInfoSerialize
                    return new SensitiveFieldSerialize(sensitiveInfo);
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

    /**
     * ip脱敏
     */
    private String ip(String ip) {
        List<String> ipList = StrUtil.split(ip, '.');
        if (ipList.size() < 2) {
            return "*.*.*.*";
        }
        return ipList.get(0) + "." + ipList.get(1) + ".*.*";
    }

    /**
     * 字段隐藏
     *
     * @param str   字符串
     * @param front 前多少位不隐藏
     * @param end   后多少位不隐藏
     * @return 处理后的字段
     */
    private String hide(String str, int front, int end) {
        // 字符串不能为空
        if (StrUtil.isBlank(str)) {
            return StrUtil.EMPTY;
        }
        // 需要截取的不能小于0
        if (front < 0 || end < 0) {
            return StrUtil.EMPTY;
        }
        return StrUtil.hide(str, front, str.length() - end);
    }
}
