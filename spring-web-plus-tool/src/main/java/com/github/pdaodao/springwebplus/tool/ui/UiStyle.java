package com.github.pdaodao.springwebplus.tool.ui;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "页面组件样式-按分组方式组织")
public class UiStyle {
    @Schema(description = "尺寸")
    private Size size;

    @Schema(description = "字体")
    private Font font;

    @Schema(description = "间距")
    private Margin margin;

    @Schema(description = "边框")
    private Border border;

    @Schema(description = "背景")
    private Background background;

    @Schema(description = "flex布局")
    private Flex flex;

    @Schema(description = "position - 定位")
    private Position position;

    @Data
    public static class Size {
        private String width;
        private String height;
        private String minWidth;
        private String minHeight;
    }

    @Data
    public static class Font{
        private String fontSize;
        private String fontWeight;
        private String fontStyle;
        private String textDecoration;
        private String lineHeight;
        private String letterSpacing;
        // '默认', '次要', '成功', '警告', '危险'
        private String colorType;
        // 自定义颜色
        private String color;
        // 'left', 'center', 'right'
        private String textAlign;
    }

    @Data
    public static class Margin {
        private Integer marginTop;
        private Integer marginRight;
        private Integer marginBottom;
        private Integer marginLeft;

        private Integer paddingTop;
        private Integer paddingRight;
        private Integer paddingBottom;
        private Integer paddingLeft;
    }

    @Data
    public static class Background{
        private String backgroundColor;
        private String backgroundImage;
        private String backgroundSize;
        private String backgroundRepeat;
        private String opacity;
    }

    @Data
    public static class Border{
        private Integer borderWidth;
        private String borderColor;
        private String borderStyle;
        private Integer borderRadius;
        private String boxShadow;
    }

    @Data
    public static class Flex{
        // 'flex', 'block'
        private String display;
        // 'row', 'column'
        private String flexDirection;
        // 'start', 'center', 'space-between'
        private String justifyContent;
        // 'stretch', 'center', 'start'
        private String alignItems;
        // 'nowrap', 'wrap'
        private String flexWrap;
        // 0 (不压缩), 1 (可压缩)
        private String flexShrink;
        private Integer gap;
    }

    @Data
    public static class Position{
        // 'static', 'relative', 'absolute'
        private String position;
        private String top;
        private String right;
        private String bottom;
        private String left;
        private Integer zIndex;
        private String overflow;
    }
}
