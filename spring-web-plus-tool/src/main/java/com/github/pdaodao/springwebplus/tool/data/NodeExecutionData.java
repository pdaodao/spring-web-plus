package com.github.pdaodao.springwebplus.tool.data;

import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.lang.OptionMap;
import lombok.Data;
import java.util.List;

/**
 * 节点数据
 */
@Data
public class NodeExecutionData {
    private String endpoint;

    /**
     * 请求头
     */
    private OptionMap h = new OptionMap();

    /**
     * query查询参数
     */
    private OptionMap q = new OptionMap();

    /**
     * 数据项列表
     */
    private List<TableDataRow> list;

    /**
     * 分页信息
     */
    private PageInfo pageInfo;

    /**
     * 文件列表
     */
    private List<InputStreamWrap> files;
}