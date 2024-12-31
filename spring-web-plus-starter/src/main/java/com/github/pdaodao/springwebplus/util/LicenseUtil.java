package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.File;
import java.util.Date;

/**
 * 软件授权工具类
 */
public class LicenseUtil {
    private static final Logger logger = LoggerFactory.getLogger(LicenseUtil.class);
    public static boolean useNetwork = false;
    public static String FileName = "license.dat";
    public static byte[] key;
    // 方便可以把 license 配置到配置中心
    public static String LicenseStr = null;

    private static AES getAes() {
        return SecureUtil.aes(key);
    }

    public static void processLicense() {
        Preconditions.assertTrue(ArrayUtil.isEmpty(key), "license-secrete-key is null");
        final Long current = DateUtil.current();
        final StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(StrUtil.repeat("#", 50)).append("\n\n");
        try {
            sb.append("     machine-id:" + getMachineId()).append("\n");
            final LicenseInfo licenseInfo = LicenseUtil.readLicense();
            Preconditions.checkNotNull(licenseInfo.getExpire(), "illegal expire time");
            Long expired = licenseInfo.getExpire().getTime();
            if (StrUtil.isNotBlank(licenseInfo.getMachineId())) {
                final String id = getMachineId();
                Preconditions.checkArgument(StrUtil.equalsIgnoreCase(licenseInfo.getMachineId(), id), "invalid machine-id");
            }
            sb.append("\n");
            if (DateTimeUtil.addDays(new Date(), 5).getTime() > expired) {
                sb.append("license will expired at " + DateTimeUtil.formatDate(expired)).append("\n");
            }
            if (current > expired) {
                sb.append("license expired").append("\n");
                sb.append("sys exist").append("\n");
                logger.error(sb.toString());
                System.exit(0);
            }
            sb.append(StrUtil.format("License: {} will expired at {}", licenseInfo.getName(),
                    DateTimeUtil.formatDateTime(licenseInfo.getExpire())));
            sb.append("\n\n").append(StrUtil.repeat("#", 50));
            logger.info(sb.toString());
        } catch (Exception e) {
            sb.append(ExceptionUtil.getRootCauseMessage(e)).append("\n");
            sb.append(StrUtil.repeat("#", 50));
            logger.error(sb.toString());
            System.exit(0);
        }
    }

    public static LicenseInfo readLicense() throws Exception {
        final String json = com.github.pdaodao.springwebplus.base.util.FileUtil.loadClassPathFileStr(FileName);
        return parse(json);
    }

    public static LicenseInfo parse(String content) {
        Preconditions.checkNotEmpty(content, "文件为空");
        try {
            content = decryptPassword(content);
            return JSONUtil.toBean(content, LicenseInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal license.");
        }
    }


    /**
     * 解密密码
     *
     * @param password
     * @return
     */
    public static String decryptPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return password;
        }
        return getAes().decryptStr(password);
    }

    public static String getMachineId() {
        // 获取硬件信息
        final SystemInfo systemInfo = new SystemInfo();
        final HardwareAbstractionLayer hardware = systemInfo.getHardware();
        final CentralProcessor processor = hardware.getProcessor();
        final ComputerSystem computerSystem = hardware.getComputerSystem();

        // 获取 CPU 序列号、主板序列号、磁盘序列号
        final String cpuSerial = processor.getProcessorIdentifier().getProcessorID();
        final String motherboardSerial = computerSystem.getBaseboard().getSerialNumber();
        final String diskSerial = hardware.getDiskStores().stream()
                .findFirst().map(disk -> disk.getSerial()).orElse("unknown");

        // 将所有序列号连接
        final String uniqueInfo = cpuSerial + motherboardSerial + diskSerial;

        // 对信息进行哈希，生成唯一机器码
        return DigestUtil.sha256Hex(uniqueInfo);
    }

    public static void gen(String machineId, String name, Integer day) {
        final LicenseUtil.LicenseInfo info = new LicenseUtil.LicenseInfo();
        info.setMachineId(machineId);
        info.setName(name);
        info.setExpire(DateTimeUtil.addDays(new Date(), day));
        info.setRemark(RandomUtil.randomString(200));
        String json = JsonUtil.toJsonString(info);
        json = encryptPassword(json);
        String dir = System.getProperty("user.dir");
        dir = dir + File.separator + LicenseUtil.FileName;
        FileUtil.writeUtf8String(json, dir);
    }

    public static void main(String[] args) throws Exception {
        final String id = getMachineId();
        System.out.println(id);
    }

    public static String encryptPassword(String password) {
        if (StrUtil.isBlank(password)) {
            return password;
        }
        return getAes().encryptHex(password);
    }

    @Data
    public static class LicenseInfo {
        // 机器码
        private String machineId;
        // 名称
        private String name;

        // 过期时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date expire;

        private String remark;
    }
}