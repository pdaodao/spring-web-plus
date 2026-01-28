package com.github.pdaodao.springwebplus.tool.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.task.cron.CronExpression;
import com.github.pdaodao.springwebplus.tool.task.cron.CronSetting;
import com.github.pdaodao.springwebplus.tool.task.cron.CronSettingType;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

import java.util.Date;

/**
 * cron表达式工具类
 */
public class CronUtil {
    /**
     * 下次执行时间
     * @param cronSetting    调度配置信息
     * @param lastTime       上次执行时间
     * @return
     * @throws Exception
     */
    public static Date nextTime(final CronSetting cronSetting, Date lastTime) throws Exception {
        if(cronSetting == null){
            return null;
        }
        final Date now = DateTimeUtil.now();
        if(lastTime == null){
            lastTime = now;
        }
        if(CronSettingType.fixed == cronSetting.getType()){
            Preconditions.checkNotNull(cronSetting.getTimeInterval(), "fixed cron time is null");
            return new Date(cronSetting.getTimeInterval());
        }
        if(cronSetting.getBeginTime() != null && now.before(cronSetting.getBeginTime())){
             return null;
        }
        if(cronSetting.getEndTime() != null && now.after(cronSetting.getEndTime())){
            return null;
        }
        if(CronSettingType.cron == cronSetting.getType()){
            return nextTime(cronSetting.getCron(), lastTime);
        }
        // 按秒周期调度
        if(CronSettingType.second == cronSetting.getType()){
            return DateTimeUtil.offsetSecond(lastTime, (int)(long) cronSetting.getTimeInterval()*1);
        }
        // 按分钟调度 几分钟调度一次
        if(CronSettingType.minute == cronSetting.getType()){
            return DateTimeUtil.offsetMinute(lastTime,  (int) (long)cronSetting.getTimeInterval());
        }
        // 按小时调度 每小时的几分钟
        if(CronSettingType.hour == cronSetting.getType()){
            for(int i = 0; i <=1; i++){
                final Date d = DateTimeUtil.offsetHour(lastTime, i);
                // 指定调度的分钟
                if(cronSetting.getMinute() != null){
                    d.setMinutes(cronSetting.getMinute());
                }
                if(d.after(DateTimeUtil.offsetSecond(now, 1))){
                    return d;
                }
            }
            return now;
        }
        // 按天调度 几天调度一次
        if(CronSettingType.day == cronSetting.getType()){
            final Date d = DateTimeUtil.offsetDay(lastTime, (int) (long)cronSetting.getTimeInterval());
            if(CollUtil.isNotEmpty(cronSetting.getHours())){
                final int nowH = now.getHours();
                boolean used = false;
                for(final Integer h: cronSetting.getHours()){
                    if(h >= nowH && h < nowH + 1){
                        used = true;
                        d.setHours(h);
                        break;
                    }
                }
                if(!used){
                    d.setHours(cronSetting.getHours().get(0));
                }
            }
            // 指定调度的分钟
            if(cronSetting.getMinute() != null){
                d.setMinutes(cronSetting.getMinute());
            }
            return d;
        }
        // todo
        return null;
    }

    public static void main(String[] args) throws Exception{
//        final Date d = new Date();
//        d.setHours(11);
//        System.out.println(DateTimeUtil.formatDateTime(d));
        final CronSetting cronSetting = CronSetting.of(CronSettingType.hour, 1);
        cronSetting.setHours(ListUtil.of(3));
        cronSetting.setMinute(5);
        final Date next = CronUtil.nextTime(cronSetting, null);
        System.out.println(DateTimeUtil.formatDateTime(next));
    }


    /**
     * 下次执行时间
     * @param cron     cron表达式
     * @param lastTime 上次执行时间
     * @return
     * @throws Exception
     */
    public static Date nextTime(final String cron, Date lastTime) throws Exception {
        if(StrUtil.isBlank(cron)){
            return null;
        }
        if(lastTime == null){
            lastTime = DateTimeUtil.now();
        }
        final Date nextValidTime = new CronExpression(cron).getNextValidTimeAfter(lastTime);
        return nextValidTime;
    }
}
