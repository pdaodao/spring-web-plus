package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

public class PasswordUtil {
    private static int MD5_PWD_LENGTH = 32;

    public static void main(String[] args) {
        System.out.println(encrypt("admin", "sa"));
    }

    public static String encrypt(String password, String salt) {
        Preconditions.checkNotBlank(password, "密码不能为空.");
        if (StrUtil.isBlank(salt)) {
            salt = "";
        }
        // 如果密码长度不是32为，则进行md5加密
        if (password.length() != MD5_PWD_LENGTH) {
            password = DigestUtil.md5Hex(password);
        }
        // 将md5加密后的结果+盐，再进行sha256加密
        String encryptPassword = DigestUtil.sha256Hex(password + salt);
        return encryptPassword;
    }
}
