package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "大模型问答系统返回数据")
public class LLMResponse {
    @Schema(description = "内容")
    private String content;

    @Schema(description = "分析阶段")
    private String phase;

    @Schema(description = "数据表数据")
    private TableData tableData;

    @Schema(description = "引用")
    private List<DocResource> sources;

    /**
     * 引用来源
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DocResource{
        @Schema(description = "文档id")
        private String id;
        @Schema(description = "来源标题")
        private String title;
        @Schema(description = "来源url")
        private String url;
        @Schema(description = "来源摘要")
        private String snippet;
    }

    public static void main(String[] args) {
        final LLMResponse r = new LLMResponse();
        r.setContent("hello world.");
        final String tt = JsonUtil.toJsonString(r);
        System.out.println(tt);
    }
}