package com.github.pdaodao.springwebplus.base.auth;

import com.github.pdaodao.springwebplus.base.pojo.SysLog;

public interface SysLogListener {
    void onSave(final SysLog sysLog);
}
