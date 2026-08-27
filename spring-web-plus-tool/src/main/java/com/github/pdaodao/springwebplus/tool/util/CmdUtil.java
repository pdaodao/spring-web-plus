package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CmdUtil {

    public static void main(String[] args) throws Exception{
        final List<String> lines = run("/Users/peng/Downloads/DataRoom-master/dataRoomFront", ListUtil.of("ls", "-lah"));
        System.out.println(StrUtil.join("\n", lines));
    }

    public static List<String> run(final String basePath, final List<String> command) throws Exception{
        try {
            final ProcessBuilder pb = new ProcessBuilder(command);
            if(StrUtil.isNotBlank(basePath)){
                pb.directory(FileUtil.file(basePath));
            }
            pb.redirectErrorStream(true);
            final Process proc = pb.start();
            final List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
            proc.waitFor();
            return lines;
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw e;
        }
    }
}
