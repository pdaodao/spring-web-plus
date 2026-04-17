package com.github.pdaodao.springwebplus.tool.ui;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "图形显示配置")
public class UiChartOption {
    @Schema(description = "图形显示类型")
    private String type;

    @Schema(description = "x轴配置")
    private XAxis xAxis = new XAxis();

    @Schema(description = "y轴配置")
    private YAxis yAxis = new YAxis();

    @Schema(description = "z轴配置")
    private ZAxis zAxis = new ZAxis();

    @Schema(description = "自定义模板")
    private String template;

    public boolean empty(){
        return yAxis == null || CollUtil.isEmpty(yAxis.fields);
    }

    @Data
    @Schema(description = "x轴配置")
    public static class XAxis{
        @Schema(description = "value、category")
        private String type = "category";

        @Schema(description = "轴标题")
        private String title;

        @Schema(description = "轴字段")
        private String field;

        @Schema(description = "分组字段")
        private String groupField;

        @Schema(description = "x轴是否添加区域缩放组件")
        private Boolean xDataZoom = false;
    }

    @Data
    @Schema(description = "y轴配置")
    public static class YAxis{
        @Schema(description = "value、category")
        private String type = "value";

        @Schema(description = "y轴字段列表")
        private List<YFieldItem> fields = new ArrayList<>();

        @Schema(description = "轴标题")
        private String title;

        @Schema(description = "是否多y轴")
        private Boolean multiY = false;

        @Schema(description = "是否堆叠显示")
        private Boolean stack = false;

        @Schema(description = "是否标注最小最大")
        private Boolean minMax = false;

        @Schema(description = "是否标注平均值")
        private Boolean average = false;
    }

    @Data
    @Schema(description = "y轴字段项")
    public static class YFieldItem{
        @Schema(description = "echarts图形显示类型")
        private String type;

        @Schema(description = "轴字段")
        private String field;

        @Schema(description = "标题")
        private String title;

        @Schema(description = "饼图半径")
        private String radius;
    }

    @Data
    @Schema(description = "z轴配置")
    public static class ZAxis{
        @Schema(description = "轴字段")
        private String field;

        @Schema(description = "轴标题")
        private String title;
    }
}
