package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.TableRowData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "大模型问答系统请求数据")
public class LLMRequest {
    @Schema(description = "问答场景id")
    private String appId;

    @Schema(description = "场景的阶段如生成sql，查询数据, 归因分析，预测分析等")
    private String phase;

    @Schema(description = "模型id")
    private String modelId;

    @Schema(description = "会话id")
    private String sessionId;

    @Schema(description = "问答消息id")
    private String msgId;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "当前用户id")
    private String userId;

    @Schema(description = "团队-租户id")
    private String teamId;

    @Schema(description = "数据源id")
    private String dbId;

    @Schema(description = "数据表id")
    private List<String> tableIds;

    @Schema(description = "其他表单数据")
    private TableRowData formData;
}