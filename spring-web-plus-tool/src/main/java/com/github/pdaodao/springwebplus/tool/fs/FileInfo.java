package com.github.pdaodao.springwebplus.tool.fs;

import lombok.Data;

@Data
public class FileInfo {
    private String name;
    private String path;
    private String contentType;
    private Long size;
}