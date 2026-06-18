package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.dao.SysApiKeyDao;
import com.github.pdaodao.springwebplus.entity.SysApiKey;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;

/**
 * api-key工具类型
 */
public class SysApiKeyUtil {
    /**
     * 校验是否有 api-key
     * @return
     */
    public static SysApiKey check(){
        String apiKey = RequestUtil.getFromHead("Api-Key");
        if(StrUtil.isBlank(apiKey)){
            apiKey = RequestUtil.getFromHead("Authorization");
        }
        Preconditions.checkNotBlank(apiKey, "apiKey is empty.");
        apiKey = StrUtil.replaceFirst(apiKey, "Bearer ", "");
        final SysApiKeyDao dao = SpringUtil.getBean(SysApiKeyDao.class);
        dao.getByApiKey(apiKey);
        final SysApiKey sysApiKey = dao.getByApiKey(apiKey);
        Preconditions.checkNotNull(sysApiKey, "apiKey is null for {}", apiKey);
        return sysApiKey;
    }
}
