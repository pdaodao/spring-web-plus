package com.github.pdaodao.springwebplus.ai.store;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Map;

@Data
@Schema(description = "向量化文本")
public class AiEmbedText {
    @Schema(description = "主键值")
    private String id;

    @Schema(description = "命名空间,用作区分不同地方的数据 数据表 接口 术语 知识库等")
    private String namespace;

    @Schema(description = "团队-租户id")
    private String teamId;

    @Schema(description = "主题:知识库id、数据源id等")
    private String topic;

    @Schema(description = "内部类型:表、字段、pdf、word等")
    private String type;

    @Schema(description = "文档id:文件id、数据表id等")
    private String docId;

    @Schema(description = "英文名称编码：表名、字段名等")
    private String name;

    @Schema(description = "中文名称:表名称、字段名称等")
    private String title;

    @Schema(description = "内容文本")
    private String content;

    @Schema(description = "内容向量化")
    private float[] embedding;

    @Schema(description = "得分")
    private Double score;

    @Schema(description = "其他元信息")
    private Map<String, Object> meta;

    public static AiEmbedText of(final String text) {
        final AiEmbedText a = new AiEmbedText();
        a.setContent(text);
        return a;
    }

    public String getContent() {
        if(StrUtil.isNotBlank(content)){
            return content;
        }
        return StrUtil.join(":", name, title);
    }

    @Override
    public String toString() {
        return content;
    }
}