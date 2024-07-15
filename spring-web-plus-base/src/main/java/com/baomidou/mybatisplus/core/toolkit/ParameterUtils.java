package com.baomidou.mybatisplus.core.toolkit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pdaodao.springwebplus.base.util.PageHelper;

import java.util.Map;
import java.util.Optional;

/**
 * 拷贝自mybatis-plus 追加了两行代码
 */
public class ParameterUtils {

    private ParameterUtils() {

    }

    /**
     * 查找分页参数
     *
     * @param parameterObject 参数对象
     * @return 分页参数
     */
    public static Optional<IPage> findPage(Object parameterObject) {
        if (parameterObject != null) {
            if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    if (entry.getValue() != null && entry.getValue() instanceof IPage) {
                        return Optional.of((IPage) entry.getValue());
                    }
                }
            } else if (parameterObject instanceof IPage) {
                return Optional.of((IPage) parameterObject);
            }
        }
        // 加入如下代码
        if (PageHelper.holder.get() != null) {
            return Optional.of(PageHelper.holder.get());
        }
        return Optional.empty();
    }
}
