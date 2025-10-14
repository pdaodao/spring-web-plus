package com.github.pdaodao.springwebplus.ai.pojo;

import com.github.pdaodao.springwebplus.tool.data.NameTitle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ChatDocItems {
    @Schema(description = "文档-数据表列表")
    private List<NameTitle> docList;

}
