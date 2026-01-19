package com.github.pdaodao.springwebplus.ai.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.dao.AiChatAppDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;

/**
 * 大模型问答相关数据库交互工具类
 */
public class AiChatDaoUtil {
    /**
     * 获取应用场景信息
     *
     * @param id
     * @return
     */
    public static AiChatApp getAppById(final String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }
        final AiChatApp m = SpringUtil.getBean(AiChatAppDao.class).info(id);
        return m;
    }

    /**
     * 获取模型配置信息
     *
     * @param id
     * @return
     */
    public static AiChatModel getModelById(final String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }
        final AiChatModel m = SpringUtil.getBean(AiChatModelDao.class).detail(id);
        return m;
    }
}