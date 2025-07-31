package com.github.pdaodao.springwebplus.base.pojo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "带时间查询")
public class WithTimeQuery extends PageRequestParam{
    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "终止时间")
    private String endTime;

    @JsonIgnore
    public Date getStartDate(){
        if(StrUtil.isBlank(startTime)){
            return null;
        }
        // 没有时间
        if(!StrUtil.contains(startTime, ":")){
            final Date d = DateTimeUtil.tryParse(startTime, DateTimeUtil.DATE_FORMATTER, DateTimeUtil.DATE_FORMATTER_SLASH);
            if(d != null){
                return DateTimeUtil.beginOfDay(d);
            }
        }
        final Date d = DateTimeUtil.tryParse(startTime, DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER_SLASH);
        return d;
    }

    @JsonIgnore
    public Date getEndDate(){
        if(StrUtil.isBlank(endTime)){
            return null;
        }
        // 没有时间
        if(!StrUtil.contains(endTime, ":")){
            final Date d = DateTimeUtil.tryParse(endTime, DateTimeUtil.DATE_FORMATTER, DateTimeUtil.DATE_FORMATTER_SLASH);
            if(d != null){
                return  DateTimeUtil.endOfDay(d);
            }
        }
        final Date d = DateTimeUtil.tryParse(endTime, DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER_SLASH);
        return d;
    }
}
