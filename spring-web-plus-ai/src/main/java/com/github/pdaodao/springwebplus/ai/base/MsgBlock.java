package com.github.pdaodao.springwebplus.ai.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pdaodao.springwebplus.tool.data.TableData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class MsgBlock {
    @Schema(description = "回答响应id")
    private String respId;

    @Schema(description = "消息块id")
    private String id;

    @Schema(description = "该消息是否结束")
    private Boolean isEnd;

    @Schema(description = "消息类型标识")
    private MsgType type;

    @Schema(description = "文本内容")
    private String text;

    @Schema(description = "数据表数据")
    private TableData tableData;

    @Schema(description = "文本消息的引用")
    private List<Annotation> annotations;

    @Schema(description = "图片/文件内容 base64")
    private String base64;

    // text/plain, text/markdown
    @Schema(description = "图片类型 image/jpeg, image/png, audio/wav,video/mp4,application/pdf")
    private String mimeType;

    @Schema(description = "图片请求地址")
    private String url;

    @Schema(description = "错误信息")
    private String error;

    @Schema(description = "耗时ms")
    private Long cost;

//    private String query;
//
//    private Object extras;

    // 函数名称
//    private String name;
//    // 函数参数
//    private Object args;
//
//    private String index;
//
//    // 非标准
//    private Object value;


    public static MsgBlock ofText(final String content){
        final MsgBlock msgBlock = new MsgBlock();
        msgBlock.setType(MsgType.text);
        msgBlock.setText(content);
        return msgBlock;
    }

    public static MsgBlock of(final MsgType msgType, final String text){
        final MsgBlock msgBlock = new MsgBlock();
        msgBlock.setType(msgType);
        msgBlock.setText(text);
        return msgBlock;
    }

    public MsgBlock setIsEnd(){
        isEnd = true;
        return this;
    }

    @Data
    public static class Annotation {
        private String id;
        private String title;
        private String url;
        private String snippet;
    }
}
