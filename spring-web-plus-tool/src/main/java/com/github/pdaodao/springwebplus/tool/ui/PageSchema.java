package com.github.pdaodao.springwebplus.tool.ui;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 页面Schema - 顶层设计
 * 统一模型：PageSchema 就是 Component
 * @author pdaodao
 */
@Data
@Schema(description = "页面Schema")
public class PageSchema {
    @Schema(description = "组件实例ID(前端生成uuid保证唯一)")
    private String id;

    @Schema(description = "组件类型,page、form、modal、input等")
    private String type;

    @Schema(description = "属性名称变量名称")
    private String name;

    @Schema(description = "显示标题组件label")
    private String title;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "页面布局样式配置")
    private LayoutConfig layoutConfig = new LayoutConfig();

    @Schema(description = "组件输入参数（URL参数/路由参数）")
    private List<PageVariable> variables = new ArrayList<>();

    @Schema(description = "子组件")
    private List<PageSchema> children = new ArrayList<>();

    @Schema(description = "组件数据和数据显示配置")
    private DataConfig dataConfig = new DataConfig();

    @Schema(description = "联动配置")
    private List<EventAction> actions = new ArrayList<>();

    @Schema(description = "子页面（抽屉/弹窗）")
    private List<PageSchema> subPages = new ArrayList<>();

    @Schema(description = "组件值校验规则")
    private List<CheckRule> checkRules = new ArrayList<>();

    @Data
    @Schema(description = "组件布局配置")
    public static class LayoutConfig {
        private String layout = "flow";
        // 主轴对齐
        private String justify;
        // 交叉轴对齐
        private String alignItems;
        private Integer gutter = 20;
        private Integer span;
        // private String width;
        private String height;
        private String x;
        private String y;

        private Integer labelWidth = 100;
        private String labelPosition = "right";
        // 其他显示属性考虑通用性可以继续在这里补充
        private Boolean hidden = false;
        private Boolean disabled = false;
        private Boolean readonly = false;
        private Boolean required = false;
        // 样式设置
        private Map<String, String> ui = new HashMap<>();
        private String css;
    }


    @Data
    @Schema(description = "页面-组件参数-变量")
    public static class PageVariable implements Serializable {
        @Schema(description = "参数名")
        private String name;

        @Schema(description = "参数标题")
        private String title;

        @Schema(description = "默认值")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String defaultValue;

        @Schema(description = "标准字段类型")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private DataType dataType;

        @Schema(description = "是否必填")
        private Boolean required;

        @Schema(description = "格式,如文件后缀,时间格式等")
        protected String format;

        @Schema(description = "变量来源 url/query/cookie/session/api或者是组件id")
        private String from;

        @Schema(description = "字段角色 如图形中的x、y等")
        private String role;

        @Schema(description = "显示配置如align、width等")
        private Map<String, String> ui = new HashMap<>();
    }


    @Data
    @Schema(description = "联动配置")
    public static class EventAction{
        @Schema(description = "触发事件click..")
        private String event;

        @Schema(description = "条件表达式")
        private String when;

        @Schema(description = "目标组件id")
        private String targetId;

        @Schema(description = "目标组件行为:reload、hidden、show...")
        private String targetAction;

        @Schema(description = "参数传递 主要使用name和from字段")
        private List<PageVariable> variableMapping = new ArrayList<>();
    }

    @Data
    @Schema(description = "组件值校验规则")
    public static class CheckRule{
        @Schema(description = "校验类型 notblank、email类型、不重复、reg正则定义等")
        private String type;
        @Schema(description = "规则内容")
        private String content;
    }


    @Data
    @Schema(description = "组件数据项配置")
    public static class DataConfig {
        @Schema(description = "数据类型 api、static、dict")
        private String type;

        @Schema(description = "接口地址")
        private String apiPath;

        @Schema(description = "接口id")
        private String apiId;

        @Schema(description = "接口标题")
        private String apiTitle;

        @Schema(description = "请求方法")
        private String method = "post";

        @Schema(description = "是否需要分页")
        private Boolean enablePage;

        @Schema(description = "静态数据值 如input和文本显示,表格数据等")
        private String value;

        @Schema(description = "请求参数")
        private List<PageVariable> params = new ArrayList<>();

        @Schema(description = "显示字段-表格显示时特别要使用 其他根据情况使用 图形的x和y轴配置")
        private List<PageVariable> displays = new ArrayList<>();

        @Schema(description = "数据展示方式配置如表格显示控制首尾冻结等、echarts图表x,y轴等的配置")
        private String dataDisplayConfig;

        // 依赖的组件ID（这些组件值变化时刷新数据）
        // private List<String> dependOn;
    }

}