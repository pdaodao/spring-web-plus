package com.github.pdaodao.springwebplus.tool.task.cron;

import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.Data;

import java.util.Date;
import java.util.List;

// 定时调度配置
@Data
public class CronSetting {
    // 调度类型
    private CronSettingType type;

    // cron 表达式
    private String cron;

    // 间隔时间
    private Long timeInterval = 1L;

    // 小时列表 (0-23)
    private List<Integer> hours;

    // 分钟 (0-59)
    private Integer minute;

    // 开始时间
    private Date beginTime;

    // 结束时间
    private Date endTime;

    public static final CronSetting of(final CronSettingType type, long timeInterval){
        Preconditions.checkNotNull(type, "CronTimeType is null.");
        final CronSetting setting = new CronSetting();
        setting.setType(type);
        setting.setTimeInterval(timeInterval);
        return setting;
    }

    public static final CronSetting ofSecond(long timeInterval){
        final CronSetting setting = new CronSetting();
        setting.setType(CronSettingType.second);
        setting.setTimeInterval(timeInterval);
        return setting;
    }

    public static final CronSetting ofMinute(long timeInterval){
        final CronSetting setting = new CronSetting();
        setting.setType(CronSettingType.minute);
        setting.setTimeInterval(timeInterval);
        return setting;
    }

    public static final CronSetting ofHour(long timeInterval){
        final CronSetting setting = new CronSetting();
        setting.setType(CronSettingType.hour);
        setting.setTimeInterval(timeInterval);
        return setting;
    }

    public static final CronSetting ofDay(long timeInterval){
        final CronSetting setting = new CronSetting();
        setting.setType(CronSettingType.day);
        setting.setTimeInterval(timeInterval);
        return setting;
    }

    public static final CronSetting ofCron(final String cron){
        Preconditions.checkNotBlank(cron, "cron is blank.");
        final CronSetting setting = new CronSetting();
        setting.setType(CronSettingType.cron);
        setting.setCron(cron);
        return setting;
    }
}
