package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.frame.ThreadLocalManager;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.dao.SysConfigDao;
import com.github.pdaodao.springwebplus.entity.SysConfig;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import java.util.Date;
import java.util.List;

/**
 * 系统参数配置工具类
 */
public class SysConfigUtil {
    public static final ThreadLocal<List<SysConfig>> holder = new ThreadLocal<>();
    static {
        ThreadLocalManager.add(holder);
    }

    public static Boolean getAsBoolean(final String key, final Boolean defaultValue){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final String v = getByKey(key);
        if(StrUtil.isBlank(v)){
            return defaultValue;
        }
        return DataValueUtil.toBoolean(v);
    }

    public static Integer getAsInteger(final String key, final Integer defaultValue){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final String v = getByKey(key);
        if(StrUtil.isBlank(v)){
            return defaultValue;
        }
        return DataValueUtil.toInt(v);
    }

    public static Long getAsLong(final String key, final Long defaultValue){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final String v = getByKey(key);
        if(StrUtil.isBlank(v)){
            return defaultValue;
        }
        return DataValueUtil.toLong(v);
    }

    public static Date getAsDate(final String key, final Date defaultValue){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final String v = getByKey(key);
        if(StrUtil.isBlank(v)){
            return defaultValue;
        }
        return DataValueUtil.toDate(v);
    }

    public static Double getAsDouble(final String key, final Double defaultValue){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final String v = getByKey(key);
        if(StrUtil.isBlank(v)){
            return defaultValue;
        }
        return DataValueUtil.toDouble(v);
    }

    public static String getByKey(final String key){
        Preconditions.checkNotBlank(key, "config key is blank.");
        final SysConfigDao dao = sysConfigDao();
        List<SysConfig> list = holder.get();
        if(list == null){
            list = dao.all();
            holder.set(list);
        }
        if(CollUtil.isEmpty(list)){
            return null;
        }
        for(final SysConfig f: list){
            if(StrUtil.equalsIgnoreCase(key, f.getConfigKey())){
                return f.getConfigValue();
            }
        }
        return null;
    }

    private static SysConfigDao sysConfigDao(){
        return SpringUtil.getBean(SysConfigDao.class);
    }
}
