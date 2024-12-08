package com.github.pdaodao.springwebplus.tool.lang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.util.ServiceLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.pdaodao.springwebplus.tool.db.core.DbType;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;

import java.io.File;
import java.io.FileFilter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 插件类加载器
 */
public class PluginClassLoaderFactory {
    public static final String PLUGINS = "plugins";
    public static final String MYDOTTXT = "my.txt";

    // 插件根目录
    public static String PluginRootPath;

    // key: 为插件目录名称
    private static Cache<String, ClassLoader> map = Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterAccess(Duration.ofHours(72))
            .build();

    // 功能点到插件目录名的映射
    private static Cache<String, String> fnKeyMap = Caffeine.newBuilder()
            .maximumSize(2000)
            .expireAfterAccess(Duration.ofHours(1))
            .build();

    static {
        final String dir = System.getProperty("user.dir");
        PluginRootPath = FilePathUtil.pathJoin(dir, PLUGINS);
    }

    /**
     * 清除
     */
    public static void clear() {
        map.invalidateAll();
        fnKeyMap.invalidateAll();
    }

    /**
     * 加载数据库驱动相关的类加载器
     *
     * @param dbType
     * @return
     */
    public static ClassLoader of(DbType dbType) {
        if (dbType == null) {
            return currentThreadClassLoader();
        }
        return of(dbType.name());
    }

    public static <T> T ofSpi(final String key, final Class<T> clazz) {
        try (final ThreadContextClassLoader classLoader = threadContextOf(key)) {
            return ServiceLoaderUtil.loadFirstAvailable(clazz);
        }
    }


    public static ThreadContextClassLoader threadContextOf(final String key) {
        final ClassLoader classLoader = of(key);
        return ThreadContextClassLoader.of(classLoader);
    }


    /**
     * 通过功能点 获取类加载器
     *
     * @param fnKey 如 数据库类型，组件编码
     * @return
     */
    public static ClassLoader of(String fnKey) {
        if (StrUtil.isBlank(fnKey)) {
            return currentThreadClassLoader();
        }
        fnKey = fnKey.trim().toLowerCase();
        String plugin = fnKeyMap.getIfPresent(fnKey);
        if (StrUtil.isBlank(plugin)) {
            plugin = findPluginName(fnKey);
        }
        if (StrUtil.isBlank(plugin)) {
            return currentThreadClassLoader();
        }
        ClassLoader classLoader = map.getIfPresent(plugin);
        if (classLoader != null) {
            return classLoader;
        }
        classLoader = pluginClassLoader(plugin);
        if (classLoader != null) {
            return classLoader;
        }
        return currentThreadClassLoader();
    }

    private static ClassLoader currentThreadClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }


    /**
     * 获取插件下的所有 jar 文件, jar 目录和 lib 目录
     *
     * @param plugin
     * @return
     */
    private static synchronized ClassLoader pluginClassLoader(final String plugin) {
        final ClassLoader cc = map.getIfPresent(plugin);
        if (cc != null) {
            return cc;
        }
        final String pluginPath = FilePathUtil.pathJoin(PluginRootPath, plugin);
        if (!FileUtil.exist(pluginPath)) {
            return null;
        }
        final List<File> jars = jars(pluginPath);
        if (CollUtil.isEmpty(jars)) {
            return null;
        }
        // 去掉已经存在的 jar
        final String[] cps = System.getProperty("java.class.path").split(File.pathSeparator);
        final Set<String> sysJars = new HashSet<>();
        for (final String cp : cps) {
            sysJars.add(FileNameUtil.getName(cp));
        }
        final JarClassLoader jarClassLoader = new JarClassLoader();
        for (final File file : jars) {
            if (!sysJars.contains(file.getName())) {
                jarClassLoader.addJar(file);
            }
        }
        map.put(plugin, jarClassLoader);
        return jarClassLoader;
    }

    /**
     * 找到目录下的 jar
     *
     * @param path
     * @return
     */
    private static List<File> jars(final String path) {
        if (!FileUtil.exist(path)) {
            return new ArrayList<>();
        }
        return FileUtil.loopFiles(FileUtil.newFile(path), 2, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return FileUtil.getName(pathname).endsWith(".jar");
            }
        });
    }


    /**
     * 找到插件所在目录 相对目录 为该plugin的名称
     *
     * @param fnKey
     * @return
     */
    private static String findPluginName(final String fnKey) {
        if (!FileUtil.exist(PluginRootPath)) {
            return null;
        }
        final List<File> files = FileUtil.loopFiles(FileUtil.newFile(PluginRootPath), 2, new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals(MYDOTTXT);
            }
        });
        if (CollUtil.isEmpty(files)) {
            return null;
        }
        for (final File file : files) {
            final List<String> lines = FileUtil.readUtf8Lines(file);
            final File p = FileUtil.getParent(file, 1);
            lines.stream().forEach(t -> {
                fnKeyMap.put(t.trim().toLowerCase(), p.getName());
            });
            final String ss = StrUtil.join(",", lines).toLowerCase() + ",";
            if (ss.contains(fnKey.trim().toLowerCase() + ",")) {
                return p.getName();
            }
        }
        return null;
    }

}