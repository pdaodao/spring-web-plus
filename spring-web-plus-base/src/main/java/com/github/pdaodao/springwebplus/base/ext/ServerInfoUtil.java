package com.github.pdaodao.springwebplus.base.ext;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.system.*;
import cn.hutool.system.oshi.CpuInfo;
import cn.hutool.system.oshi.OshiUtil;
import com.github.pdaodao.springwebplus.base.ext.pojo.*;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取系统服务信息
 **/
public class ServerInfoUtil {

    /**
     * 获取服务器信息
     *
     * @return
     */
    public static ServerInfo getServerInfo() {
        Cpu cpu = getCpu();
        Memory memory = getMemory();
        Jvm jvm = getJvm();
        List<Disk> diskList = getDiskList();
        OperatingSystemInfo operatingSystemInfo = getOperatingSystemInfo();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setCpu(cpu);
        serverInfo.setMemory(memory);
        serverInfo.setJvm(jvm);
        serverInfo.setDiskList(diskList);
        serverInfo.setDisk(currentDisk());
        serverInfo.setOperatingSystemInfo(operatingSystemInfo);
        return serverInfo;
    }

    /**
     * 获取CPU信息
     *
     * @return
     */
    public static Cpu getCpu() {
        try {
            CpuInfo cpuInfo = OshiUtil.getCpuInfo();
            // CPU核心数
            Integer cpuNum = cpuInfo.getCpuNum();
            // 总CPU使用率：获取用户+系统的总的CPU使用率
            double used = cpuInfo.getUsed();
            // CPU系统使用率
            double sys = cpuInfo.getSys();
            // CPU用户使用率
            double user = cpuInfo.getUser();
            // CPU当前空闲率
            double free = cpuInfo.getFree();
            Cpu cpu = new Cpu();
            cpu.setCpuNum(cpuNum);
            cpu.setSys(sys);
            cpu.setUser(user);
            cpu.setFree(free);
            cpu.setUsed(used);
            return cpu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取内存信息
     *
     * @return
     */
    public static Memory getMemory() {
        try {
            GlobalMemory globalMemory = OshiUtil.getMemory();
            // 总内存
            long total = globalMemory.getTotal();
            // 空闲内存
            long available = globalMemory.getAvailable();
            // 已使用的
            long used = total - available;
            String totalUnit = FileUtil.readableFileSize(total);
            String availableUnit = FileUtil.readableFileSize(available);
            String usedUnit = FileUtil.readableFileSize(used);
            BigDecimal availableRate = NumberUtil.round(available * 1.0 / total * 100, 2);
            BigDecimal usedRate = NumberUtil.sub(100, availableRate);
            Memory memory = new Memory();
            memory.setTotal(total);
            memory.setAvailable(available);
            memory.setUsed(used);
            memory.setTotalUnit(totalUnit);
            memory.setAvailableUnit(availableUnit);
            memory.setUsedUnit(usedUnit);
            memory.setAvailableRate(availableRate);
            memory.setUsedRate(usedRate);
            return memory;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取JVM信息
     *
     * @return
     */
    public static Jvm getJvm() {
        try {
            RuntimeInfo runtimeInfo = SystemUtil.getRuntimeInfo();
            // JVM已分配内存
            long totalMemory = runtimeInfo.getTotalMemory();
            // JVM最大内存
            long maxMemory = runtimeInfo.getMaxMemory();
            // JVM最大可用内存
            long usableMemory = runtimeInfo.getUsableMemory();
            // JVM已分配内存中的剩余空间
            long freeMemory = runtimeInfo.getFreeMemory();
            String totalMemoryUnit = FileUtil.readableFileSize(totalMemory);
            String maxMemoryUnit = FileUtil.readableFileSize(maxMemory);
            String usableMemoryUnit = FileUtil.readableFileSize(usableMemory);
            String freeMemoryUnit = FileUtil.readableFileSize(freeMemory);
            JvmInfo jvmInfo = SystemUtil.getJvmInfo();
            // JVM名称
            String name = jvmInfo.getName();
            // JVM厂商
            String vendor = jvmInfo.getVendor();
            JavaInfo javaInfo = SystemUtil.getJavaInfo();
            // java版本
            String version = javaInfo.getVersion();
            // java按照路径
            String home = SystemUtil.get("java.home");
            OSProcess currentProcess = OshiUtil.getCurrentProcess();
            // 当前进程ID
            int processID = currentProcess.getProcessID();
            // 当前进程启动时间
            long startTime = currentProcess.getStartTime();
            // 当前进程已使用时间
            long userTime = currentProcess.getUserTime();
            // 当前进程已使用时间描述
            String userTimeDesc = DateTimeUtil.getDiffDaySecond(userTime);
            // 当前进程Cpu使用率
            double processCpuLoadCumulative = currentProcess.getProcessCpuLoadCumulative();
            BigDecimal processCpuRate = NumberUtil.round(processCpuLoadCumulative * 100, 2);
            Jvm jvm = new Jvm();
            jvm.setTotalMemory(totalMemory);
            jvm.setMaxMemory(maxMemory);
            jvm.setUsableMemory(usableMemory);
            jvm.setFreeMemory(freeMemory);
            jvm.setTotalMemoryUnit(totalMemoryUnit);
            jvm.setMaxMemoryUnit(maxMemoryUnit);
            jvm.setUsableMemoryUnit(usableMemoryUnit);
            jvm.setFreeMemoryUnit(freeMemoryUnit);
            jvm.setName(name);
            jvm.setVendor(vendor);
            jvm.setVersion(version);
            jvm.setHome(home);
            jvm.setProcessId(processID);
            jvm.setStartTime(new Date(startTime));
            jvm.setUserTime(userTime);
            jvm.setUserTimeDesc(userTimeDesc);
            jvm.setProcessCpuRate(processCpuRate);
            return jvm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取磁盘列表
     *
     * @return
     */
    public static List<Disk> getDiskList() {
        try {
            final SystemInfo systemInfo = new SystemInfo();
            final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
            final FileSystem fileSystem = operatingSystem.getFileSystem();
            final List<OSFileStore> fileStores = fileSystem.getFileStores();
            final List<Disk> disks = new ArrayList<>();
            for (OSFileStore fileStore : fileStores) {
                disks.add(diskInfo(fileStore));
            }
            return disks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取操作系统信息
     *
     * @return
     */
    public static OperatingSystemInfo getOperatingSystemInfo() {
        try {
            OsInfo osInfo = SystemUtil.getOsInfo();
            String name = osInfo.getName();
            String arch = osInfo.getArch();
            String version = osInfo.getVersion();
            HostInfo hostInfo = SystemUtil.getHostInfo();
            String address = hostInfo.getAddress();
            OperatingSystemInfo operatingSystemInfo = new OperatingSystemInfo();
            operatingSystemInfo.setName(name);
            operatingSystemInfo.setArch(arch);
            operatingSystemInfo.setVersion(version);
            operatingSystemInfo.setAddress(address);
            return operatingSystemInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Disk diskInfo(final OSFileStore fileStore) {
        // 磁盘路径
        String mount = fileStore.getMount();
        // 文件系统类型
        String type = fileStore.getType();
        // 磁盘名称
        String name = fileStore.getName();
        // 磁盘总大小
        long totalSpace = fileStore.getTotalSpace();
        // 磁盘可用空间大小
        long usableSpace = fileStore.getUsableSpace();
        // 磁盘已使用空间大小
        long usedSpace = totalSpace - usableSpace;
        String totalSpaceUnit = FileUtil.readableFileSize(totalSpace);
        String usableSpaceUnit = FileUtil.readableFileSize(usableSpace);
        String usedSpaceUnit = FileUtil.readableFileSize(usedSpace);
        BigDecimal usableRate = NumberUtil.round(usableSpace * 1.0 / totalSpace * 100, 2);
        BigDecimal usedRate = NumberUtil.sub(100.0, usableRate);
        Disk disk = new Disk();
        disk.setMount(mount);
        disk.setType(type);
        disk.setName(name);
        disk.setTotalSpace(totalSpace);
        disk.setUsableSpace(usableSpace);
        disk.setUsedSpace(usedSpace);
        disk.setTotalSpaceUnit(totalSpaceUnit);
        disk.setUsableSpaceUnit(usableSpaceUnit);
        disk.setUsedSpaceUnit(usedSpaceUnit);
        disk.setUsableRate(usableRate);
        disk.setUsedRate(usedRate);
        return disk;
    }

    /**
     * 当前应用所在盘
     *
     * @return
     */
    public static Disk currentDisk() {
        try {
            final String currentDir = System.getProperty("user.dir");
            final SystemInfo systemInfo = new SystemInfo();
            final OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
            final FileSystem fileSystem = operatingSystem.getFileSystem();
            final List<OSFileStore> fileStores = fileSystem.getFileStores();
            for (final OSFileStore fileStore : fileStores) {
                if (currentDir.startsWith(fileStore.getLogicalVolume())) {
                    return diskInfo(fileStore);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        final Disk dist = currentDisk();
        System.out.println(dist.getTotalSpaceUnit() + " : " + dist.getUsedSpaceUnit());
    }

}
