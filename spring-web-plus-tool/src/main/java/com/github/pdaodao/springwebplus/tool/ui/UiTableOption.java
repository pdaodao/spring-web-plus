package com.github.pdaodao.springwebplus.tool.ui;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "表格显示方式配置")
public class UiTableOption {
    @Schema(description = "表格列")
    private List<UiTableField> fields = new ArrayList<>();

    @Schema(description = "是否分页显示")
    private Boolean showPage = true;

    @Schema(description = "是否斑马条纹")
    private Boolean stripe  = false;

    @Schema(description = "固定列 0:不固定 正数:左边几列 负数:右边几列")
    private Integer fixed = 0;

    @Schema(description = "表格内容最大高度为element-plus表格组件属性max-height")
    private Integer maxHeight;

    @Schema(description = "是否显示下载数据按钮")
    private Boolean download = false;

    @Data
    @Schema(description = "表格列字段")
    public static class UiTableField{
        @Schema(description = "字段")
        private String name;

        @Schema(description = "字段名称标题")
        private String title;

        @Schema(description = "标准字段类型")
        private DataType dataType;

        @Schema(description = "字段显示宽度")
        private String width;

        @Schema(description = "对齐方式")
        private String align;

        @Schema(description = "是否支持排序")
        private Boolean sortable = false;

        @Schema(description = "显示类型 text,tag,image,link,date,datetime,time")
        private String showType = "text";
    }

    public boolean empty(){
        return CollUtil.isEmpty(fields);
    }
}
