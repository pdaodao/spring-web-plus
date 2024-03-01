package com.github.apengda.springwebplus.service;

import cn.hutool.core.util.StrUtil;
import com.github.apengda.springwebplus.entity.SysUser;
import com.github.apengda.springwebplus.starter.pojo.CurrentUserInfo;
import com.github.apengda.springwebplus.starter.pojo.LoginInfo;
import com.github.apengda.springwebplus.starter.util.Preconditions;
import com.github.apengda.springwebplus.util.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户登录服务
 */
@Service
public interface LoginService {
    /**
     * 登录
     *
     * @param loginInfo
     * @return
     */
     CurrentUserInfo login(final LoginInfo loginInfo);

    /*
     * 退出登录
     */
     void logout(CurrentUserInfo userInfo);
}
