package com.github.pdaodao.springwebplus.task.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "日志文件日志行")
public class LogResult {
    @Schema(description = "起始行号从0开始")
    private Integer from;

    @Schema(description = "结束行号，下次从该值开始")
    private Integer to;

    @Schema(description = "是否结束,对于运行完成的任务为true")
    private Boolean isEnd;

    @Schema(description = "日志数据行")
    private List<String> lines;

    public LogResult addLine(final String line) {
        if (lines == null) {
            lines = new ArrayList<>();
        }
        lines.add(line);
        return this;
    }
}
