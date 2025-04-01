package com.github.pdaodao.springwebplus.tool.data;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.lang.OptionMap;
import lombok.Data;
import java.util.List;

/**
 * 批量数据
 */
@Data
public class StreamRows {
    /**
     * 数据标签：如表名，topic名称等
     */
    private String tag;
    /**
     * 数据项列表
     */
    private List<StreamRow> rows;
    /**
     *  元信息
     */
    private OptionMap meta;
    /**
     * 是否数据结束了
     */
    private Boolean isEnd;

    public OptionMap setMeta(final String key, final Object value){
        if(meta == null){
            meta = new OptionMap();
        }
        if(StrUtil.isBlank(key)){
            return meta;
        }
        meta.put(key, value);
        return meta;
    }
}