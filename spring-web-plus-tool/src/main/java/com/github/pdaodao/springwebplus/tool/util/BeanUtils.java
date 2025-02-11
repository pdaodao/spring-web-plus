package com.github.pdaodao.springwebplus.tool.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.util.pojo.EntityDiffWrap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 实体属性工具类
 */
public class BeanUtils {

    /**
     * id为空或者为0
     *
     * @param pid
     * @return
     */
    public static boolean isPidTop(final String pid) {
        return StrUtil.isBlank(pid) || StrUtil.equals("0", pid);
    }

    /**
     * 内容是否发生变更
     *
     * @return
     */
    public static boolean isDiff(final Object from, final Object to, final String... fields) {
        if (ObjectUtil.isNull(from) || ObjectUtil.isNull(to)) {
            return false;
        }
        if (ArrayUtil.isEmpty(fields)) {
            return false;
        }
        final Map<String, Object> fromMap = BeanUtil.beanToMap(from, fields);
        final Map<String, Object> toMap = BeanUtil.beanToMap(to, fields);
        final String fromStr = JSONUtil.toJsonStr(fromMap);
        final String toStr = JSONUtil.toJsonStr(toMap);
        return StrUtil.equals(fromStr, toStr);
    }

    public static <T> List<Map<String, ?>> toMapList(final List<T> list) {
        if (CollUtil.isEmpty(list)) {
            return ListUtil.empty();
        }
        final List<Map<String, ?>> ret = new ArrayList<>();
        for (final T t : list) {
            ret.add(beanToMap(t));
        }
        return ret;
    }

    /**
     * 对比新旧数据 得到需要新增 更新 删除的
     *
     * @param list
     * @param oldList
     * @param keyFun
     * @param <T>
     * @return
     */
    public static <T> EntityDiffWrap<T> diff(final List<T> list, final List<T> oldList, final Function<T, Serializable> keyFun) {
        final EntityDiffWrap result = new EntityDiffWrap();
        //1. 旧数据map
        final LinkedCaseInsensitiveMap<T> oldMap = new LinkedCaseInsensitiveMap<>();
        if (CollUtil.isNotEmpty(oldList)) {
            for (final T t : oldList) {
                oldMap.put(ObjectUtil.toString(keyFun.apply(t)), t);
            }
        }
        //2. 新数据 map
        final LinkedCaseInsensitiveMap<T> map = new LinkedCaseInsensitiveMap<>();
        //3. 和老数据对比得到需要新增 和 修改的数据
        if (CollUtil.isNotEmpty(list)) {
            for (final T t : list) {
                map.put(ObjectUtil.toString(keyFun.apply(t)), t);
                final T old = oldMap.get(keyFun.apply(t));
                if (old != null) {
                    // 合并属性
                    mergeProperties(old, t);
                    result.addUpdate(t);
                } else {
                    result.addInsert(t);
                }
            }
        }
        //4. 得到需要删除的数据
        if (CollUtil.isNotEmpty(oldList)) {
            for (final T t : oldList) {
                if (!map.containsKey(keyFun.apply(t))) {
                    result.addDelete(t);
                }
            }
        }
        result.setOldMap(oldMap);
        return result;
    }


    public static Map<String, Object> beanToMap(Object bean, String... properties) {
        return BeanUtil.beanToMap(bean, properties);
    }

    /**
     * 浅拷贝属性
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target,
                CopyOptions.create().setTransientSupport(false)
                        .setIgnoreCase(true)
                        .setIgnoreProperties(ignoreProperties));
    }

    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target,
                CopyOptions.create().setTransientSupport(false)
                        .setIgnoreNullValue(true)
                        .setIgnoreProperties(ignoreProperties));
    }


    public static <T> List<T> copyToList(final List<?> list, final Class<T> targetType, String... ignoreProperties) {
        return BeanUtil.copyToList(list, targetType, CopyOptions.create().setTransientSupport(false)
                .setIgnoreProperties(ignoreProperties));
    }

    public static <T> List<T> copyToListIgnoreTransient(final List<?> list, final Class<T> targetType, String... ignoreProperties) {
        return BeanUtil.copyToList(list, targetType, CopyOptions.create().setTransientSupport(true)
                .setIgnoreProperties(ignoreProperties));
    }

    public static void copyPropertiesIgnoreTransient(Object source, Object target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target,
                CopyOptions.create().setTransientSupport(true)
                        .setIgnoreProperties(ignoreProperties));
    }


    /**
     * 浅拷贝 忽略目标中已经存在的值的字段
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void mergeProperties(Object source, Object target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target,
                CopyOptions.create().setTransientSupport(false)
                        .setOverride(false)
                        .setIgnoreProperties(ignoreProperties));
    }

    /**
     * 合并属性 忽略 Transient
     *
     * @param source
     * @param target
     * @param ignoreProperties
     */
    public static void mergePropertiesIgnoreTransient(Object source, Object target, String... ignoreProperties) {
        BeanUtil.copyProperties(source, target,
                CopyOptions.create().setTransientSupport(true)
                        .setOverride(false)
                        .setIgnoreProperties(ignoreProperties));
    }

    /**
     * 是否含有类
     * @param className
     * @return
     */
    public static boolean hasClass(final String className){
        try{
            Class.forName(className);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
