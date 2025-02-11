package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.github.pdaodao.springwebplus.base.entity.WithChildren;
import com.github.pdaodao.springwebplus.base.entity.WithDelete;

import java.util.*;
import java.util.function.Function;

public class IdUtil {
    public static long workerId = 1l;

    public static final String BaseChar = "123456789abcdefghjkmnpqrstvwxyz";
    public static final int Size = BaseChar.length();

    private static String convertToAlphanumeric(long number) {
        final StringBuilder result = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % Size);
            result.insert(0, BaseChar.charAt(remainder));
            number /= Size;
        }
        return result.toString();
    }

    /**
     * 雪花id 转字符串
     *
     * @return
     */
    public static String snowIdString() {
        final Long timeMillis = cn.hutool.core.util.IdUtil.getSnowflake(workerId).nextId();
        final String identifier = convertToAlphanumeric(timeMillis);
        return identifier;
    }

    public static Long snowId() {
        final Long timeMillis = cn.hutool.core.util.IdUtil.getSnowflake(workerId).nextId();
        return timeMillis;
    }

    public static void main(String[] args) {
        for(int i = 0; i < 30; i++){
            System.out.println(snowId());
        }
    }

    /**
     * 组装为树形结构
     *
     * @param list
     * @param idFun
     * @param pidFun
     * @param <E>
     * @param <K>
     * @return
     */
    public static <E extends WithChildren, K> List<E> toTree(List<E> list, Function<E, K> idFun, Function<E, K> pidFun) {
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        Assert.notNull(idFun);
        Assert.notNull(pidFun);
        final Map<K, E> map = new LinkedHashMap<>();
        for (E entity : list) {
            map.put(idFun.apply(entity), entity);
        }
        // 去掉删除的数据和其子数据
        Boolean isWithDeleted = null;
        final Set<K> deletedFlag = new HashSet<>();
        // 防止重复数据
        final Set<K> idProcessed = new HashSet<>();
        final List<E> tree = new ArrayList<>();
        for (final E entity : list) {
            final K id = idFun.apply(entity);
            final K pid = pidFun.apply(entity);
            if (idProcessed.contains(id)) {
                continue;
            }
            idProcessed.add(id);
            // 去掉逻辑删除的
            if (isWithDeleted == null) {
                isWithDeleted = entity instanceof WithDelete ? true : false;
            }
            if (deletedFlag.contains(pid)) {
                deletedFlag.add(id);
                continue;
            }
            if (isWithDeleted && ObjectUtil.equal(true, ((WithDelete) entity).getIsDeleted())) {
                deletedFlag.add(id);
                continue;
            }
            if (ObjectUtil.isEmpty(pid) || ObjectUtil.equal(0, pid) || ObjectUtil.equal(pid, id)) {
                tree.add(entity);
                continue;
            }
            final E pEntity = map.get(pid);
            if (pEntity == null) {
                tree.add(entity);
                continue;
            }
            if (pEntity.getChildren() == null) {
                pEntity.setChildren(new ArrayList());
            }
            pEntity.getChildren().add(entity);
        }
        return tree;
    }
}
