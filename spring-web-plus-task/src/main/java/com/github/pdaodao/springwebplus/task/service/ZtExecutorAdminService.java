package com.github.pdaodao.springwebplus.task.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZtExecutorAdminService {
    private final ZtExecutorAdminScheduler adminScheduler;
    public void setIsAdmin(final boolean is){
        adminScheduler.setIsAdmin(is);
    }

}
