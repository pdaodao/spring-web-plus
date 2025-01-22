package com.github.pdaodao.springwebplus.bflow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.github.pdaodao.springwebplus.base.entity.AutoIdWithTimeEntity;
import com.github.pdaodao.springwebplus.base.frame.TableFieldIndex;
import com.github.pdaodao.springwebplus.bflow.pojo.InstanceState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.logging.log4j.CloseableThreadContext;

import java.util.Date;

@Data
@TableName(value = "bf_hist_instance", autoResultMap = true)
@Schema(description = "历史流程实例表")
public class BfHisInstanceEntity extends BfInstanceEntity {

    /**
     * 状态 0，活动 1，结束 更多查看 {@link InstanceState}
     */
    protected InstanceState state;
    /**
     * 结束时间
     */
    protected Date endTime;
    /**
     * 处理耗时
     */
    protected Long duration;
}