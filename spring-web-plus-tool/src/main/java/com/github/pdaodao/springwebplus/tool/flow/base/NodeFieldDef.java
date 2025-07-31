package com.github.pdaodao.springwebplus.tool.flow.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.RichSqlValue;
import com.github.pdaodao.springwebplus.tool.sql.core.WhereOperator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "参数定义")
public class NodeFieldDef {
    @Schema(description = "变量-字段")
    private String name;

//    @Schema(description = "别名")
//    private String alias;

    @Schema(description = "中文名称")
    private String title;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "数据类型")
    private DataType dataType;

    @Schema(description = "字典id")
    private String dicId;

    @Schema(description = "比较符")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private WhereOperator op;

    @Schema(description = "是否必填")
    protected Boolean required;

    @Schema(description = "值")
    private RichSqlValue value;

    @Schema(description = "默认值")
    protected String defaultValue;

    @Schema(description = "格式,如文件后缀,时间格式等")
    protected String format;

    @Schema(description = "子项")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected List<NodeFieldDef> children;
}
