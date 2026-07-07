package com.github.pdaodao.springwebplus.base.pojo;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "带时间查询")
public class WithTimeQuery extends PageRequestParam{
    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "终止时间")
    private String endTime;

    @JsonIgnore
    public LocalDateTime getStartDate(){
        if(StrUtil.isBlank(startTime)){
            return null;
        }
        // 没有时间
        if(!StrUtil.contains(startTime, ":")){
            final LocalDate d = DateTimeUtil.tryParseDate(startTime, DateTimeUtil.DATE_FORMATTER, DateTimeUtil.DATE_FORMATTER_SLASH);
            if(d != null){
                return d.atTime(0, 0);
            }
        }
        final LocalDateTime d = DateTimeUtil.tryParse(startTime, DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER_SLASH);
        return d;
    }

    @JsonIgnore
    public LocalDateTime getEndDate(){
        if(StrUtil.isBlank(endTime)){
            return null;
        }
        // 没有时间
        if(!StrUtil.contains(endTime, ":")){
            final LocalDate d = DateTimeUtil.tryParseDate(endTime, DateTimeUtil.DATE_FORMATTER, DateTimeUtil.DATE_FORMATTER_SLASH);
            if(d != null){
                return d.plusDays(1).atTime(0, 0);
            }
        }
        final LocalDateTime d = DateTimeUtil.tryParse(endTime, DateTimeUtil.DATE_TIME_FORMATTER, DateTimeUtil.DATE_TIME_FORMATTER_SLASH);
        return d;
    }

    public static void main(String[] args) {
        final String startTime = "2026-07-06";
        final LocalDateTime d = DateTimeUtil.tryParse(startTime, DateTimeUtil.DATE_FORMATTER, DateTimeUtil.DATE_FORMATTER_SLASH);
        System.out.println(d);
    }
}
