package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.entity.SnowIdWithTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@TableName(value = "bf_flow", autoResultMap = true)
@Schema(description = "流程定义表")
public class BfFlowEntity extends AutoIdWithTimeEntity {
    @Schema(description = "名称")
    @Size(max = 100)
    private String title;

    @Schema(description = "图标")
    @Size(max = 100)
    private String icon;

    @Schema(description = "说明")
    @Size(max = 300, message = "说明不能超过300个字")
    private String remark;

    @Schema(description = "流程启动页面地址")
    private String startUrl;

    @Schema(description = "流程定义内容")
    @Size(max = 8000)
    private String content;

    /**
     * 流程状态 0，不可用 1，可用 2，历史版本
     */
//    protected Integer processState;

    @Schema(description = "版本")
    private Integer version;

    @Schema(description = "排序")
    private Integer seq;
}