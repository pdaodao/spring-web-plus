package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.util.StrUtil;

import java.io.File;

/**
 * 文件路径工具
 */
public class FilePathUtil {

    public static final String PathSeparator = "/";

    /**
     * 获取父路径
     */
    public static String getParent(String path) {
        if (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        int endIndex = Math.max(path.lastIndexOf("/"), path.lastIndexOf("\\"));
        return endIndex > -1 ? path.substring(0, endIndex) : null;
    }

    /**
     * 本地文件存储 路径合并
     *
     * @param paths
     * @return
     */
    public static String localFilePathJoin(String... paths) {
        return join(File.separator, paths);
    }

    /**
     * 合并文件路径
     *
     * @param paths
     * @return
     */
    public static String pathJoin(String... paths) {
        return join(PathSeparator, paths);
    }

    /**
     * 合并路径
     */
    private static String join(final String separator, String... paths) {
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (String path : paths) {
            if (StrUtil.isBlank(path)) {
                continue;
            }
            path = path.trim();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            if (index++ > 0) {
                if (!path.startsWith(separator)) {
                    path = separator + path;
                }
            }
            sb.append(path);
        }
        return sb.toString();
    }

    /**
     * 返回去掉跟路径的路径
     * @param fullpath
     * @param rootPath
     * @return
     */
    public static String dropRootPath(final String fullpath, final String rootPath){
        if(StrUtil.isBlank(rootPath)){
            return fullpath;
        }
        return fullpath.replaceFirst(rootPath, "");
    }
}
