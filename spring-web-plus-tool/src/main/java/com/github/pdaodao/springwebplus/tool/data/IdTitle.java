package com.github.pdaodao.springwebplus.tool.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class IdTitle {
    @Schema(description = "id")
    private String id;

    @Schema(description = "名称标题")
    private String title;

    public static IdTitle of(final String id, final String title){
        final IdTitle d = new IdTitle();
        d.setId(id);
        d.setTitle(title);
        return d;
    }
}